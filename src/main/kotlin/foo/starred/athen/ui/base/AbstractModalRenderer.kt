package foo.starred.athen.ui.base

import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.ui.IZoneType
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import net.minecraft.client.gui.GuiGraphics

abstract class AbstractModalRenderer<T : IEntryView>(
    protected val mw: Int,
    protected val mh: Int,
    protected val fh: Int,
    protected val padding: Int
) {
    var open = false
    var entry: T? = null

    protected abstract val create: String
    protected abstract val edit: String
    protected abstract val zone0: IZoneType
    protected abstract val zone1: IZoneType
    protected open val dropdown: Boolean = false

    fun draw(graphics: GuiGraphics, mx: Int, my: Int, sw: Int, sh: Int, zones: MutableList<UIZone>) {
        graphics.rectangle(0, 0, sw, sh, Mocha.Crust.withAlpha(0.6f))
        val x0 = (sw - mw) / 2
        val y0 = (sh - mh) / 2
        val fw = mw - padding * 2

        graphics.rectangle(x0, y0, mw, mh, Mocha.Base.argb)
        graphics.outline(x0, y0, mw, mh, 1, Mocha.Surface0.argb)

        graphics.extractText(if (entry == null) create else edit, x0 + padding, y0 + padding + 2, false, Mocha.Mauve.argb)
        graphics.rectangle(x0, y0 + 24, mw, 1, Mocha.Surface0.argb)

        val cy = y0 + 34
        fields(graphics, mx, my, x0, y0, cy, fw, zones)

        val hw = fw / 2 - 4
        val y1 = y0 + mh - fh - padding
        val x1 = x0 + padding
        val x2 = x1 + hw + 8

        graphics.rectangle(x0 + padding, y1 - 8, fw, 1, Mocha.Surface0.argb)

        graphics.rectangle(x2, y1, hw, fh, if (!dropdown && hovered(x2, y1, hw, fh, true)) Mocha.Surface2.argb else Mocha.Surface1.argb)
        graphics.outline(x2, y1, hw, fh, 1, Mocha.Green.argb)
        graphics.extractText("Save", x2 + (hw - client.font.width("Save")) / 2, y1 + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Green.argb)
        zones.add(UIZone(x2, y1, hw, fh, zone0))

        graphics.rectangle(x1, y1, hw, fh, if (!dropdown && hovered(x1, y1, hw, fh, true)) Mocha.Surface2.argb else Mocha.Surface1.argb)
        graphics.outline(x1, y1, hw, fh, 1, Mocha.Red.argb)
        graphics.extractText("Cancel", x1 + (hw - client.font.width("Cancel")) / 2, y1 + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Red.argb)
        zones.add(UIZone(x1, y1, hw, fh, zone1))

        overlays(graphics, mx, my, x0, y0, fw, zones)
    }

    protected abstract fun fields(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, cy: Int, fw: Int, zones: MutableList<UIZone>)

    protected open fun overlays(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, fw: Int, zones: MutableList<UIZone>) {}

    fun close() {
        open = false
        entry = null
        onClose()
    }

    protected open fun onClose() {}
}
