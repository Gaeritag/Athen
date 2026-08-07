package foo.starred.athen.modules.impl.render.highlight.ui.renderers

import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.modules.impl.render.highlight.ui.data.HighlightEntry
import foo.starred.athen.modules.impl.render.highlight.ui.data.UIZoneType
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.base.AbstractListRenderer
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import net.minecraft.client.gui.GuiGraphics

class ListRenderer(
    height: Int,
    spacing: Int,
    fh: Int,
    padding: Int
) : AbstractListRenderer<HighlightEntry>(height, spacing, fh, padding) {
    override val string = "No highlights"

    override fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, entry: HighlightEntry, open: Boolean, zones: MutableList<UIZone>) {
        val b = !open && hovered(x, y, w, height, true)

        graphics.rectangle(x, y, w, height, if (b) Mocha.Surface1.argb else Mocha.Surface0.argb)
        graphics.outline(x, y, w, height, 1, Mocha.Overlay0.argb)

        var cx = x + padding

        val y0 = y + (height - 12) / 2
        graphics.rectangle(cx, y0, 12, 12, entry.color or 0xFF000000.toInt())
        graphics.outline(cx, y0, 12, 12, 1, Mocha.Overlay0.argb)
        cx += 12 + padding

        if (entry.max != -1) {
            val hp = "${entry.max} HP"
            val hw = client.font.width(hp) + 8
            graphics.rectangle(cx, y + (height - fh) / 2, hw, fh, Mocha.Surface2.argb)
            graphics.outline(cx, y + (height - fh) / 2, hw, fh, 1, Mocha.Crust.argb)
            graphics.extractText(hp, cx + 4, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Peach.argb)
            cx += hw + padding
        }

        graphics.enableScissor(cx, y, cx + (w - (cx - x) - 60 - padding * 2), y + height)
        graphics.extractText(entry.name, cx, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        graphics.disableScissor()

        val x0 = x + w - 60 - padding * 2
        val b0 = !open && hovered(x0, y + (height - fh) / 2, 40, fh, true)
        graphics.rectangle(x0, y + (height - fh) / 2, 40, fh, if (b0) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(x0, y + (height - fh) / 2, 40, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("Edit", x0 + (40 - client.font.width("Edit")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(x0, y + (height - fh) / 2, 40, fh, UIZoneType.ENTRY_EDIT, entry.index))

        val x1 = x + w - 20 - padding
        val b1 = !open && hovered(x1, y + (height - fh) / 2, 20, fh, true)
        graphics.rectangle(x1, y + (height - fh) / 2, 20, fh, if (b1) Mocha.Red.argb else Mocha.Base.argb)
        graphics.outline(x1, y + (height - fh) / 2, 20, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("×", x1 + (21 - client.font.width("×")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(x1, y + (height - fh) / 2, 20, fh, UIZoneType.ENTRY_DELETE, entry.index))
    }
}