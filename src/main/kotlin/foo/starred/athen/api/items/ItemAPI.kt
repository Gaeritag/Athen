@file:Suppress("ObjectPrivatePropertyName", "FunctionName", "Unused")

package foo.starred.athen.api.items

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.accessors.hovered
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.builders.option.ConfigOptionBuilder
import foo.starred.athen.ducks.item.stack.ItemStackDuck.Companion.`athen$cached$tooltip`
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.on
import foo.starred.kontext.abstraction.gui.MinecraftGui

@LoadableAthenModule
object ItemAPI { // TODO: make this check the parent config of added keys if the watch list gets too big
    private val pressed = mutableSetOf<Int>()
    private val `watched$tooltip` = mutableListOf<() -> Int>()

    init {
        on<GuiEvent.Slots.Input.Hover> {
            slot.item.takeIf { !it.isEmpty }?.`athen$cached$tooltip` = null
        }

        on<GuiEvent.Input.Key.Press> {
            if (!pressed.add(keyEvent.key)) return@on
            val screen = MinecraftGui.container ?: return@on

            if (`watched$tooltip`.any { it() == keyEvent.key }) screen.hovered?.item?.`athen$cached$tooltip` = null
        }

        on<GuiEvent.Input.Key.Release> {
            if (!pressed.remove(keyEvent.key)) return@on
            val screen = MinecraftGui.container ?: return@on

            if (`watched$tooltip`.any { it() == keyEvent.key }) screen.hovered?.item?.`athen$cached$tooltip` = null
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
    }

    fun ConfigOptionBuilder<InputConstants.Key>.`watch$tooltip`(): ConfigOptionBuilder<InputConstants.Key> = apply {
        resolve { `watched$tooltip`.add { it.value.value } }
    }
}
