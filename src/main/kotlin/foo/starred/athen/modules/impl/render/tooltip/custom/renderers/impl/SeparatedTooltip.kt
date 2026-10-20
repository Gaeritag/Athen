package foo.starred.athen.modules.impl.render.tooltip.custom.renderers.impl

import foo.starred.athen.modules.impl.render.tooltip.custom.CustomTooltip
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.base.ITooltipRenderer
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.data.TooltipContext
import foo.starred.cascade.graphics.extensions.blur.blur
import foo.starred.cascade.graphics.extensions.rectangle.hollow.hollowRectangle
import foo.starred.cascade.graphics.extensions.rectangle.solid.rectangle
import foo.starred.cascade.graphics.extensions.scissor.scissor
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.util.FormattedCharSequence

object SeparatedTooltip : ITooltipRenderer {
    override fun TooltipContext.render() {
        val width1 = width + 8
        val height1 = graphics.guiHeight()
        val thickness = CustomTooltip.`border$width`

        val header = components[0]
        val i0 = header.getHeight(font) + 6

        val body = components.drop(1).let { if ((it.firstOrNull() as? ClientTextTooltip)?.text?.equals(FormattedCharSequence.EMPTY) == true) it.drop(1) else it }
        val i1 = body.sumOf { it.getHeight(font) }
        val height2 = i0 + 4 + i1 + 8

        val y1 = if (height2 < height1 - 40) (y - 4).coerceIn(20, height1 - 20 - height2) else 20
        val x1 = x - 4
        val y2 = y1 + 4

        graphics.box(x1, y1, width1, i0, thickness)

        val text = (header as? ClientTextTooltip)?.text
        if (text != null) {
            val x = if (CustomTooltip.`header$centered`) (x + (width1 - 8) / 2) - font.width(text) / 2 else x
            MinecraftFontRenderer.extract(graphics, text, x, y2, CustomTooltip.`text$shadow`)
        }

        header.extractImage(font, x, y2, width, height, graphics)

        val x2 = x - 4
        val y3 = y1 + i0 + 4
        val height3 = minOf(i1 + 6, height1 - 20 - y3).coerceAtLeast(0)

        val y4 = CustomTooltip.scroll(i1, height3 - 6)
        val height4 = if (CustomTooltip.`scroll$infinite`) (if (y4 > 0) height3 - y4 else i1 + y4 + 6).coerceIn(0, height1 - 20 - y3) else height3

        graphics.box(x2, y3, width1, height4, thickness)
        graphics.components(font, body, x, x2, y3, width1, height4, y3 + 4 + y4.coerceAtMost(0), width, i1)
        graphics.fade(x2, y3, width1, height4, y4, i1)
    }

    private fun GuiGraphicsExtractor.box(x: Int, y: Int, width: Int, height: Int, thickness: Int) {
        val x = x.toFloat()
        val y = y.toFloat()
        val width = width.toFloat()
        val height = height.toFloat()
        val thickness = thickness.toFloat()

        if (CustomTooltip.background) {
            if (CustomTooltip.`background$blur`) blur(x, y, width, height, CustomTooltip.`background$color`, CascadeGeometricRadius.ZERO, CustomTooltip.`background$strength`)
            else rectangle(x, y, width, height, CustomTooltip.`background$color`)
        }

        if (CustomTooltip.border && thickness > 0) {
            hollowRectangle(x, y, width, height, thickness, if (CustomTooltip.`border$rarity`) CustomTooltip.color else CustomTooltip.`border$color`, inset = false)
        }
    }

    private fun GuiGraphicsExtractor.components(font: Font, list: List<ClientTooltipComponent>, x0: Int, x1: Int, y1: Int, width: Int, height: Int, y0: Int, width0: Int, height0: Int) {
        scissor(x1, y1, width, height) {
            var y00 = y0
            for (c in list) {
                c.extractText(this, font, x0, y00)
                y00 += c.getHeight(font)
            }

            y00 = y0
            for (c in list) {
                c.extractImage(font, x0, y00, width0, height0, this)
                y00 += c.getHeight(font)
            }
        }
    }

    private fun GuiGraphicsExtractor.fade(x: Int, y: Int, width: Int, height: Int, y1: Int, height1: Int) {
        val color1 = CustomTooltip.`background$color` or 0xFF000000.toInt()
        val colo2 = color1 and 0x00FFFFFF

        scissor(x, y, width, height) {
            if (y1 < 0) {
                fillGradient(x, y, x + width, y + 18, color1, colo2)
            }

            if (y1 > 0 || height1 + y1 > height) {
                fillGradient(x, y + height - 18, x + width, y + height, colo2, color1)
            }
        }
    }
}
