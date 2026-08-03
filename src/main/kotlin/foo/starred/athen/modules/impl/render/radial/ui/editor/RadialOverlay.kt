package foo.starred.athen.modules.impl.render.radial.ui.editor

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl.RectanglePrimitive
import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.modules.impl.render.radial.RadialMenu
import foo.starred.athen.modules.impl.render.radial.utils.RadialRenderState
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import foo.starred.snowbird.utils.mouseSX
import foo.starred.snowbird.utils.mouseSY
import net.minecraft.client.gui.GuiGraphics

class RadialOverlay(private val panel: RectanglePrimitive) : IPrimitiveElement<RadialOverlay>() {
    override var x: Int = 0
    override var y: Int = 0
    override var width: Int = 0
    override var height: Int = 0
    override var color: Int = -1

    override fun render(graphics: GuiGraphics) {
        if (!visible) return
        val working = RadialEditor.working.takeIf { it.isNotEmpty() } ?: return

        val i0 = RadialEditor.main
        val i1 = RadialEditor.sub

        val x0 = panel.x + 160
        val y0 = panel.y + 160

        val mx = mouseSX
        val my = mouseSY

        val bool = RadialMenu.type == 0 && i1 >= 0 && i0 in working.indices
        val current = if (bool) working[i0].sub else working
        val num = maxOf(3, current.size)

        val x1 = mx - x0.toFloat()
        val y1 = my - y0.toFloat()
        val hc = x1 * x1 + y1 * y1 < 144f
        val ex = if (!bool) RadialEditor.extra() else emptyList()

        var main = -1
        var sub = -1

        if (!hc && x1 * x1 + y1 * y1 >= 225f) {
            if (!bool && RadialMenu.type == 2 && i0 in working.indices) {
                val hit = RadialRenderState.hitRing(mx, my, x0, y0, num, RadialMenu.radius2, ex.map { it.first }, false, RadialMenu.thickness)
                if (hit != -1) sub = hit
            }

            if (!bool && sub == -1 && RadialMenu.type == 1 && i0 in working.indices) {
                val hit = RadialRenderState.hitNested(mx, my, x0, y0, num, RadialMenu.radius2, i0, working[i0].sub.size, false, RadialMenu.thickness)
                if (hit != -1) {
                    main = i0
                    sub = hit
                }
            }

            if (main == -1 && sub == -1) {
                main = RadialRenderState.hit(mx, my, x0, y0, num, RadialMenu.radius1, RadialMenu.radius2)
            }
        }

        for (i in current.indices) {
            val (sx, sy) = RadialRenderState.anchor(x0, y0, num, RadialMenu.radius1, RadialMenu.radius2, i)
            //~ if >= 26.1 'renderItem(' -> 'item('
            graphics.renderItem(current[i].item, sx - 8, sy - 8)
        }

        if (!bool && RadialMenu.type == 1 && i0 in working.indices) {
            for (j in working[i0].sub.indices) {
                val (sx, sy) = RadialRenderState.nested(x0, y0, num, RadialMenu.radius2, i0, j, RadialMenu.thickness)
                //~ if >= 26.1 'renderItem(' -> 'item('
                graphics.renderItem(working[i0].sub[j].item, sx - 8, sy - 8)
            }
        }

        if (!bool && RadialMenu.type == 2) {
            for ((i, s) in ex) {
                val (sx, sy) = RadialRenderState.ring(x0, y0, num, RadialMenu.radius2, i, RadialMenu.thickness)
                //~ if >= 26.1 'renderItem(' -> 'item('
                graphics.renderItem(s.item, sx - 8, sy - 8)
            }
        }

        val back = bool || (RadialMenu.type == 2 && i0 in working.indices && i1 >= 0)
        val str = if (back) "←" else "✕"

        graphics.extractText(str, x0 - client.font.width(str) / 2, y0 - client.font.lineHeight / 2, false, if (hc) Mocha.Mauve.argb else Mocha.Subtext0.argb)

        val label = if (hc) (if (back) "Back" else "Exit") else {
            if (sub != -1) working.getOrNull(i0)?.sub?.getOrNull(sub)?.name
            else current.getOrNull(main)?.name
        }

        if (label != null && hovered(panel.x, panel.y, 320, 320, true)) {
            val tw = client.font.width(label)
            val lmx = mx.toInt() + 12
            val lmy = my.toInt() - 4

            graphics.rectangle(lmx - 5, lmy - 5, tw + 10, client.font.lineHeight + 10, Mocha.Base.argb)
            graphics.outline(lmx - 5, lmy - 5, tw + 10, client.font.lineHeight + 10, 1, Mocha.Mauve.argb)
            graphics.extractText(label, lmx, lmy, false, Mocha.Text.argb)
        }

        super.render(graphics)
    }
}