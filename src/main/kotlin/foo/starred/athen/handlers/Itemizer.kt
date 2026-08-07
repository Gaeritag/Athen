@file:Suppress("FunctionName", "ObjectPrivatePropertyName", "Unchecked_Cast", "Unused")

package foo.starred.athen.handlers

import foo.starred.athen.accessors.hovered
import foo.starred.athen.annotations.Load
import foo.starred.athen.config.ConfigBuilder
import foo.starred.athen.ducks.item.ItemStackDuck.Companion.`athen$cached$tooltip`
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*

@Load
object Itemizer { // TODO: make this check the parent config of added keys if the watch list gets too big
    private val pressed = mutableSetOf<Int>()

    private val `watched$tooltip` = mutableListOf<() -> Int>()
    private val `watched$slot` = mutableListOf<() -> Int>()

    private val `cache$slot` = WeakHashMap<Slot, Pair<ItemStack, List<(GuiGraphics, Slot) -> Unit>?>>()

    init {
        on<GuiEvent.Close.Container> {
            `cache$slot`.clear()
        }

        on<GuiEvent.Slots.Hover> {
            slot.item?.takeIf { !it.isEmpty }?.`athen$cached$tooltip` = null
        }

        on<GuiEvent.Input.Key.Press> {
            if (!pressed.add(keyEvent.key)) return@on
            val screen = client.screen as? AbstractContainerScreen<*> ?: return@on

            if (`watched$tooltip`.any { it() == keyEvent.key }) screen.hovered?.item?.`athen$cached$tooltip` = null
            if (`watched$slot`.any { it() == keyEvent.key }) screen.menu?.slots?.forEach { `cache$slot`.remove(it) }
        }

        on<GuiEvent.Input.Key.Release> {
            if (!pressed.remove(keyEvent.key)) return@on
            val screen = client.screen as? AbstractContainerScreen<*> ?: return@on

            if (`watched$tooltip`.any { it() == keyEvent.key }) screen.hovered?.item?.`athen$cached$tooltip` = null
            if (`watched$slot`.any { it() == keyEvent.key }) screen.menu?.slots?.forEach { `cache$slot`.remove(it) }
        }

        on<GuiEvent.Tooltip.Render> {
            val cached = item.`athen$cached$tooltip`

            if (cached != null && cached.first == tooltip) {
                tooltip.clear()
                tooltip.addAll(cached.second)
                return@on
            }

            val a = ArrayList(tooltip)
            GuiEvent.Tooltip.Update(item, tooltip).post()
            item.`athen$cached$tooltip` = a to ArrayList(tooltip)
        }

        on<GuiEvent.Slots.Render.Pre> {
            val cached = `cache$slot`[slot] ?: return@on
            if (cached.first != slot.item) return@on
            if (cached.second != null) return@on

            cancel()
        }

        on<GuiEvent.Slots.Render.Post> {
            val cached = `cache$slot`[slot]

            if (cached != null && cached.first == slot.item) {
                cached.second?.forEach { it(graphics, slot) }
                return@on
            }

            val renders = mutableListOf<(GuiGraphics, Slot) -> Unit>()
            val event = GuiEvent.Slots.Render.Update(graphics, slot, renders).post()

            if (event) {
                `cache$slot`[slot] = slot.item to null
                return@on
            }

            `cache$slot`[slot] = slot.item to renders.toList()
            for (r in renders) r(graphics, slot)
        }
    }

    fun ConfigBuilder.OptionBuilder<Int>.`watch$tooltip`(): ConfigBuilder.OptionBuilder<Int> = apply {
        resolve { `watched$tooltip`.add { it.value } }
    }

    fun ConfigBuilder.OptionBuilder<Int>.`watch$slot`(): ConfigBuilder.OptionBuilder<Int> = apply {
        resolve { `watched$slot`.add { it.value } }
    }
}