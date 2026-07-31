package foo.starred.athen.ui.base

import net.minecraft.client.gui.GuiGraphics
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client

abstract class AbstractListRenderer<T : IEntryView>(
    protected val height: Int,
    protected val spacing: Int,
    protected val fh: Int,
    protected val padding: Int
) {
    protected var scroll = 0
    protected var max = 0

    protected open val string = "No entries"

    fun draw(graphics: GuiGraphics, mx: Int, my: Int, lx: Int, ly: Int, lw: Int, lh: Int, entries: List<T>, modalOpen: Boolean, zones: MutableList<UIZone>) {
        if (entries.isEmpty()) return graphics.extractText(string, lx + (lw - client.font.width(string)) / 2, ly + lh / 2, false, Mocha.Subtext0.argb)

        max = maxOf(0, entries.size * (height + spacing) - spacing - lh)
        scroll = scroll.coerceIn(-max, 0)

        graphics.enableScissor(lx - 2, ly - 2, lx + lw + 2, ly + lh + 2)

        var cy = ly + scroll
        for (e in entries) {
            if (cy + height > ly - 5 && cy < ly + lh + 5) entry(graphics, mx, my, lx, cy, lw, e, modalOpen, zones)
            cy += height + spacing
        }

        graphics.disableScissor()
    }

    protected abstract fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, entry: T, open: Boolean, zones: MutableList<UIZone>)

    fun scroll(amount: Int) {
        scroll = (scroll + amount).coerceIn(-max, 0)
    }
}
