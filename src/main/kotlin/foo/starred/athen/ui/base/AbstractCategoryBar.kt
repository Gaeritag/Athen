package foo.starred.athen.ui.base

import net.minecraft.client.gui.GuiGraphics
import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.ui.IZoneType
import foo.starred.athen.ui.InputField
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered

abstract class AbstractCategoryBar(
    protected val height0: Int,
    protected val height1: Int
) {
    val nameField = InputField("Name")

    var selected: String? = null
    var creating = false
    var deleting: String? = null
    var scroll = 0

    var tooltipText: String? = null
    var tooltipColor = 0
    var tooltipX = 0
    var tooltipY = 0

    protected abstract val zone0: IZoneType
    protected abstract val zone1: IZoneType
    protected abstract val zone2: IZoneType

    protected abstract fun categories(): List<ICategoryEntry>
    abstract fun addCategory(name: String)

    fun draw(graphics: GuiGraphics, sx: Int, sy: Int, sh: Int, modalOpen: Boolean, zones: MutableList<UIZone>) {
        graphics.rectangle(sx, sy, height0, sh, Mocha.Base.argb)
        graphics.outline(sx, sy, height0, sh, 1, Mocha.Surface0.argb)

        val cats = categories()
        val lx = sx + 4
        val lw = height0 - 8
        scroll = scroll.coerceIn(-maxOf(0, (cats.size + 1) * height1 + 18 - (sh - 8)), 0)

        graphics.enableScissor(sx + 1, sy + 1, sx + height0 - 1, sy + sh - 1)

        var cy = sy + 4 + scroll
        tooltipText = null

        if (selected == null) graphics.rectangle(lx, cy, lw, height1, Mocha.Surface0.argb)
        else if (!modalOpen && hovered(lx, cy, lw, height1, true)) graphics.rectangle(lx, cy, lw, height1, Mocha.Surface0.withAlpha(0.5f))

        graphics.extractText("All", lx + 4, cy + (height1 - client.font.lineHeight) / 2 + 1, false, if (selected == null) Mocha.Mauve.argb else Mocha.Subtext0.argb)
        zones.add(UIZone(lx, cy, lw, height1, zone0, category = ""))
        cy += height1

        for ((i, cat) in cats.withIndex()) {
            val b = !modalOpen && hovered(lx, cy, lw, height1, true)

            if (deleting == cat.name) {
                graphics.rectangle(lx + 1, cy + 1, lw - 2, height1 - 2, Mocha.Red.withAlpha(0.15f))
                graphics.outline(lx + 1, cy + 1, lw - 2, height1 - 2, 1, Mocha.Red.argb)
                graphics.enableScissor(lx + 4, cy, lx + lw - 18, cy + height1)
                graphics.extractText(cat.name, lx + 4, cy + (height1 - client.font.lineHeight) / 2 + 1, false, Mocha.Red.argb)
                graphics.disableScissor()

                if (b) {
                    tooltipText = "Left click to confirm"
                    tooltipColor = Mocha.Red.argb
                    tooltipX = lx + lw + 4
                    tooltipY = cy
                }
            } else {
                if (selected == cat.name) graphics.rectangle(lx, cy, lw, height1, Mocha.Surface0.argb)
                else if (b) graphics.rectangle(lx, cy, lw, height1, Mocha.Surface0.withAlpha(0.5f))

                if (b) {
                    tooltipText = "Right click to delete"
                    tooltipColor = Mocha.Subtext0.argb
                    tooltipX = lx + lw + 4
                    tooltipY = cy
                }

                graphics.enableScissor(lx + 4, cy, lx + lw - 18, cy + height1)
                graphics.extractText(cat.name, lx + 4, cy + (height1 - client.font.lineHeight) / 2 + 1, false, when {
                    !cat.enabled -> Mocha.Overlay0.argb
                    selected == cat.name -> Mocha.Mauve.argb
                    else -> Mocha.Subtext0.argb
                })
                graphics.disableScissor()
            }

            val tx = lx + lw - 14
            val ty = cy + (height1 - 10) / 2
            val th = !modalOpen && hovered(tx, ty, 10, 10, true)
            graphics.rectangle(tx, ty, 10, 10, if (th) Mocha.Surface2.argb else Mocha.Mantle.argb)
            graphics.outline(tx, ty, 10, 10, 1, if (cat.enabled) Mocha.Green.argb else Mocha.Overlay0.argb)
            if (cat.enabled) graphics.rectangle(tx + 2, ty + 2, 6, 6, Mocha.Green.argb)

            zones.add(UIZone(tx, ty, 10, 10, zone1, i))
            zones.add(UIZone(lx, cy, lw - 18, height1, zone0, i, category = cat.name))
            cy += height1
        }

        if (creating) {
            nameField.draw(graphics, lx, cy + 2, lw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, zone2)) }
            graphics.disableScissor()
            return
        }

        if (!modalOpen && hovered(lx, cy + 2, lw, 14, true)) graphics.rectangle(lx, cy + 2, lw, 14, Mocha.Surface0.withAlpha(0.5f))
        graphics.extractText("+", lx + (lw - client.font.width("+")) / 2, cy + 2 + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(lx, cy + 2, lw, 14, zone2))
        graphics.disableScissor()
    }

    fun tooltip(graphics: GuiGraphics) {
        val str = tooltipText ?: return
        val tw = client.font.width(str)
        graphics.rectangle(tooltipX, tooltipY, tw + 8, client.font.lineHeight + 6, Mocha.Base.argb)
        graphics.outline(tooltipX, tooltipY, tw + 8, client.font.lineHeight + 6, 1, Mocha.Overlay0.argb)
        graphics.extractText(str, tooltipX + 4, tooltipY + 3, false, tooltipColor)
    }

    fun scroll(amount: Int, sh: Int) {
        val f = (categories().size + 1) * height1 + 18
        scroll = (scroll + amount).coerceIn(-maxOf(0, f - (sh - 8)), 0)
    }

    fun create0() {
        val str = nameField.value.trim()
        if (str.isNotEmpty()) addCategory(str)
        nameField.reset(true)
        creating = false
    }

    fun create1() {
        nameField.reset(true)
        creating = false
    }

    fun create() {
        creating = true
        deleting = null
        nameField.reset(true)
        nameField.focused = true
    }
}
