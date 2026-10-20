package foo.starred.athen.modules.impl.render.radial.ui.editor

import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.modules.impl.render.radial.RadialMenu
import foo.starred.athen.modules.impl.render.radial.utils.RadialRenderState
import foo.starred.cascade.graphics.extensions.rectangle.hollow.hollowRectangle
import foo.starred.cascade.graphics.extensions.rectangle.solid.rectangle
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import net.minecraft.client.gui.GuiGraphicsExtractor

class RadialOverlay(private val panel: IPrimitiveElement<*>) : IPrimitiveElement<RadialOverlay>() {
    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f
    override var color: CascadeGeometricColor = CascadeGeometricColor.WHITE

    override fun draw(graphics: GuiGraphicsExtractor) {
        val working = RadialEditor.working.takeIf { it.isNotEmpty() } ?: return

        val i0 = RadialEditor.main
        val i1 = RadialEditor.sub

        val x0 = panel.x.toInt() + 160
        val y0 = panel.y.toInt() + 160

        val mx = mouseX
        val my = mouseY

        val bool = RadialMenu.type == 0 && i1 >= 0 && i0 in working.indices
        val current = if (bool) working[i0].sub else working
        val size = maxOf(1, current.size)

        val x1 = mx - x0
        val y1 = my - y0
        val hc = x1 * x1 + y1 * y1 < 144f
        val ex = if (!bool) RadialEditor.extra() else emptyList()

        var main = -1
        var sub = -1

        if (!hc && x1 * x1 + y1 * y1 >= 225f) {
            if (!bool && RadialMenu.type == 2 && i0 in working.indices) {
                val hit = RadialRenderState.hitRing(mx, my, x0, y0, size, RadialMenu.radius2, ex.map { it.first }, false, RadialMenu.thickness)
                if (hit != -1) sub = hit
            }

            if (!bool && sub == -1 && RadialMenu.type == 1 && i0 in working.indices) {
                val hit = RadialRenderState.hitNested(mx, my, x0, y0, size, RadialMenu.radius2, i0, working[i0].sub.size, false, RadialMenu.thickness)
                if (hit != -1) {
                    main = i0
                    sub = hit
                }
            }

            if (main == -1 && sub == -1) {
                main = RadialRenderState.hit(mx, my, x0, y0, size, RadialMenu.radius1, RadialMenu.radius2)
            }
        }

        for (i in current.indices) {
            val (sx, sy) = RadialRenderState.anchor(x0, y0, size, RadialMenu.radius1, RadialMenu.radius2, i)
            graphics.item(current[i].item, sx - 8, sy - 8)
        }

        if (!bool && RadialMenu.type == 1 && i0 in working.indices) {
            for (j in working[i0].sub.indices) {
                val (sx, sy) = RadialRenderState.nested(x0, y0, size, RadialMenu.radius2, i0, j, RadialMenu.thickness)
                graphics.item(working[i0].sub[j].item, sx - 8, sy - 8)
            }
        }

        if (!bool && RadialMenu.type == 2) {
            for ((i, s) in ex) {
                val (sx, sy) = RadialRenderState.ring(x0, y0, size, RadialMenu.radius2, i, RadialMenu.thickness)
                graphics.item(s.item, sx - 8, sy - 8)
            }
        }

        val back = bool || (RadialMenu.type == 2 && i0 in working.indices && i1 >= 0)
        val str = if (back) "←" else "✕"

        MinecraftFontRenderer.extract(graphics, str, x0 - MinecraftFontMeasurer.width(str) / 2, y0 - MinecraftFontMeasurer.height / 2, false, if (hc) MochaColorScheme.Lavender.argb else MochaColorScheme.Subtext0.argb)

        val label = if (hc) (if (back) "Back" else "Exit") else {
            if (sub != -1) working.getOrNull(i0)?.sub?.getOrNull(sub)?.name
            else current.getOrNull(main)?.name
        }

        if (label != null && panel.hovered) {
            val width = MinecraftFontMeasurer.width(label)
            val mx0 = mx.toInt() + 12
            val my0 = my.toInt() - 4

            graphics.rectangle(mx0 - 5f, my0 - 5f, width + 10f, MinecraftFontMeasurer.height + 10f, MochaColorScheme.Base.argb)
            graphics.hollowRectangle(mx0 - 5f, my0 - 5f, width + 10f, MinecraftFontMeasurer.height + 10f, 1f, MochaColorScheme.Lavender.argb, inset = false)
            MinecraftFontRenderer.extract(graphics, label, mx0, my0, false, MochaColorScheme.Text.argb)
        }
    }
}
