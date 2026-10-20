@file:Suppress("ObjectPrivatePropertyName", "ObjectPropertyName", "Unused")

package foo.starred.athen.modules.impl.render.tooltip.custom

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.accessors.hovered
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.data.TooltipContext
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.impl.CombinedTooltip
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.impl.SeparatedTooltip
import foo.starred.kontext.abstraction.gui.MinecraftGui
import foo.starred.kontext.abstraction.input.impl.GenericInputState
import foo.starred.kontext.api.item.data.rarity.SkyBlockItemRarity
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.SkyBlockRarityKontextItemDataResolver
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner

@LoadableAthenModule
object CustomTooltip : Module(
    "Custom tooltip",
    "Custom tooltip rendering!",
    ConfigCategory.RENDER
) {
    private val customisation by config.group("Tooltip customisation")
    val `scroll$infinite` by customisation.switch("Infinite scroll")
    val `scroll$horizontal` by customisation.switch("Horizontal scroll", true)
    val `scroll$horizontal$key` by customisation.keybind("Horizontal keybind", InputConstants.KEY_LSHIFT)
    val `scroll$horizontal$speed` by customisation.slider("Horizontal scroll speed", 8, 1, 20, "pixels")
    val `scroll$vertical` by customisation.switch("Vertical scroll", true)
    val `scroll$vertical$speed` by customisation.slider("Vertical scroll speed", 8, 1, 20, "pixels")
    val `scroll$reset` by customisation.switch("Reset on hover")
    val `scroll$scale` by customisation.switch("Scale tooltip")
    val `scroll$scale$key` by customisation.keybind("Scale keybind", InputConstants.KEY_LCONTROL)

    private val render by config.group("Custom rendering")
    val `tooltip$style` by render.selector("Tooltip style", listOf("Combined", "Separated"), 1)
    val `header$centered` by render.switch("Centered header", true)
    val `body$visible` by render.keybind("Hide body keybind").description("Toggling only name mode will hide the actual tooltip and show only the name when it's toggled on.")
    val `text$shadow` by render.switch("Text shadows", true)

    private val _border by config.group("Border customisation")
    val border by _border.switch("Border", true)
    val `border$width` by _border.slider("Border width", 1, 0, 5)
    val `border$rarity` by _border.switch("Use rarity color", true)
    val `border$color` by _border.colorPicker("Border color", MochaColorScheme.Sky.argb)

    private val _background by config.group("Background customisation")
    val background by _background.switch("Background", true)
    val `background$color` by _background.colorPicker("Background color", MochaColorScheme.Surface0.alpha(0.9f))
    val `background$blur` by _background.switch("Background blur", true)
    val `background$strength` by _background.slider("Blur strength", 8f, 2f, 32f)

    var color: Int = `border$color`
    var name: Boolean = false
    var last: Int = 0

    var scale: Double = 1.0
    var xo: Double = 0.0
    var yo: Double = 0.0
    var mss: Double = 0.0
    var msx: Double = 0.0

    init {
        on<GuiEvent.Slots.Input.Hover> {
            if (!`scroll$reset`) {
                color = slot.item.get<SkyBlockRarityKontextItemDataResolver, SkyBlockItemRarity>()?.color?.color ?: `border$color`
                return@on
            }

            reset()
            color = slot.item.get<SkyBlockRarityKontextItemDataResolver, SkyBlockItemRarity>()?.color?.color ?: `border$color`
        }

        on<GuiEvent.Close.Any> {
            color = `border$color`
            name = false
            reset()
        }

        on<GuiEvent.Input.Key.Press> {
            if (!GenericInputState.bound(`body$visible`.value)) return@on
            if (keyEvent.key != `body$visible`.value) return@on
            if (last != Scheduler.ticks.client) return@on

            name = !name
            if (name) yo = 0.0
        }

        on<GuiEvent.Input.Mouse.Scroll> {
            if (last != Scheduler.ticks.client) return@on
            if (name) return@on

            if (`scroll$scale` && GenericInputState.pressed(`scroll$scale$key`)) {
                scale += amount * 0.1
                scale = scale.coerceIn(0.5, 3.0)
                return@on
            }

            if (`scroll$horizontal` && GenericInputState.pressed(`scroll$horizontal$key`)) {
                xo += amount * `scroll$horizontal$speed`
                return@on
            }

            if (`scroll$vertical`) {
                val n = (yo + amount * `scroll$vertical$speed`).coerceIn(mss, msx)
                yo = if (yo != 0.0 && (n > 0) != (yo > 0)) 0.0 else n
            }
        }
    }

    @JvmStatic
    fun render(graphics: GuiGraphicsExtractor, font: Font, components: List<ClientTooltipComponent>, x: Int, y: Int, positioner: ClientTooltipPositioner) {
        if (color != `border$color` && MinecraftGui.container?.hovered == null) color = `border$color`

        last = Scheduler.ticks.client
        val components = if (name) components.take(1) else components
        val single = components.size == 1

        var width = 0
        var height = if (single) -2 else 0

        for (c in components) {
            width = maxOf(width, c.getWidth(font))
            height += c.getHeight(font)
        }

        val pos = positioner.positionTooltip(graphics.guiWidth(), graphics.guiHeight(), x, y, width, height)
        val context = TooltipContext(graphics, font, components, pos.x(), pos.y(), width, height)

        val x1 = pos.x()
        val y1 = pos.y()
        val pose = graphics.pose()

        pose.pushMatrix()
        pose.translate((x1 - 4).toFloat(), (y1 - 4).toFloat())
        pose.scale(if (`scroll$scale`) scale.toFloat() else 1f)
        pose.translate(-(x1 - 4).toFloat(), -(y1 - 4).toFloat())
        pose.translate(xo.toFloat(), 0f)

        when (`tooltip$style`) {
            0 -> CombinedTooltip
            1 -> if (single) CombinedTooltip else SeparatedTooltip
            else -> null
        }?.r(context)

        pose.popMatrix()
    }

    fun scroll(content: Int, visible: Int): Int {
        mss = if (`scroll$infinite`) -content.toDouble() else -maxOf(0, content - visible).toDouble()
        msx = if (`scroll$infinite`) content.toDouble() else 0.0
        yo = yo.coerceIn(mss, msx)

        return yo.toInt()
    }

    private fun reset() {
        xo = 0.0
        yo = 0.0
        scale = 1.0

        mss = 0.0
        msx = 0.0
    }
}
