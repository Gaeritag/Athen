package foo.starred.athen.api.rendering.ui.components.impl

import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.extensions.rectangle.outline
import foo.starred.cascade.extensions.rectangle.rectangle
import foo.starred.cascade.extensions.scissor.scissor
import foo.starred.cascade.extensions.text.extractText
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.max
import kotlin.math.min

open class MultiCheckboxComponent : IPrimitiveElement<MultiCheckboxComponent>() {
    private var _height1: Int? = null

    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f
    override var color: Int = -1

    var label: String = ""
    var text: String = ""
    var selected: (Int) -> Boolean = { false }
    var onSelect: (Int) -> Unit = {}
    var scroll: Int = 0
    var open: Boolean = false

    var hovered1: Boolean = false
        private set

    var hoverY: Double = 0.0
        private set

    var items: List<String> = emptyList()
        set(value) {
            if (field == value) return
            field = value

            _height1 = min(value.size * 14, 100)
        }

    val height1: Int
        get() = _height1 ?: min(items.size * 14, 100).also { _height1 = it }

    init {
        on<MouseEvent.Press> {
            val y0 = this@MultiCheckboxComponent.y

            if (hovered1 && open) {
                val i = ((hoverY - (y0 + height) + scroll) / 14).toInt()
                if (i in items.indices) onSelect(i)
                return@on cancel()
            }

            open = !open
            if (!open) {
                root.focused = null
                return@on cancel()
            }

            parent?.children?.remove(self)
            parent?.children?.add(self)
            cancel()
        }

        on<MouseEvent.Scroll> {
            if (!hovered1) return@on
            if (!open) return@on

            val mh = height1
            val maxScroll = max(0, items.size * 14 - mh)
            scroll = max(0, min(maxScroll, scroll - (amount * 14).toInt()))
            cancel()
        }
    }

    override fun render(graphics: GuiGraphicsExtractor) {
        if (!visible) return
        if (root.focused != this) open = false
        val font = client.font ?: return

        val x = x.toInt()
        val y = y.toInt()
        val width = width.toInt()
        val height = height.toInt()

        if (label.isNotEmpty()) graphics.extractText(label, x, y - font.lineHeight - 2, false, Catppuccin.Mocha.Subtext0.argb)
        graphics.rectangle(x, y, width, height, Catppuccin.Mocha.Surface1.argb)
        graphics.outline(x, y, width, height, 1, if (open) Catppuccin.Mocha.Lavender.argb else Catppuccin.Mocha.Surface2.argb)

        graphics.extractText(text, x + 4, y + (height - font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Text.argb)
        graphics.extractText(if (open) "▾" else "▸", x + width - 12, y + (height - font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Overlay0.argb)

        if (open) {
            val height1 = height1

            graphics.rectangle(x, y + height, width, height1, Catppuccin.Mocha.Base.argb)
            graphics.outline(x, y + height, width, height1, 1, Catppuccin.Mocha.Lavender.argb)

            graphics.scissor(x, y + height + 1, width, height + height1 - 1) {
                var y0 = y + height - scroll
                for ((idx, item) in items.withIndex()) {
                    if (y0 + 14 <= y + height || y0 >= y + height + height1) {
                        y0 += 14
                        continue
                    }

                    graphics.rectangle(x, y0, width, 14, Catppuccin.Mocha.Base.argb)

                    val b = selected(idx)
                    graphics.extractText(item, x + 4, y0 + (14 - font.lineHeight) / 2 + 1, false, if (b) Catppuccin.Mocha.Lavender.argb else Catppuccin.Mocha.Text.argb)
                    if (b) graphics.extractText("✔", x + width - 14, y0 + (14 - font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Lavender.argb)

                    y0 += 14
                }
            }
        }

        super.render(graphics)
    }

    override fun contains(x: Double, y: Double): Boolean {
        val b = x >= this.x && y >= this.y && x < this.x + width && y < this.y + height
        if (b) {
            hovered1 = false
            return true
        }

        if (!open) {
            return false
        }

        if (x < this.x) return false
        if (x > this.x + width) return false
        if (y <= this.y + height) return false
        if (y > this.y + height + height1) return false

        hovered1 = true
        hoverY = y
        return true
    }

    companion object {
        inline fun multiCheckbox(block: MultiCheckboxComponent.() -> Unit): MultiCheckboxComponent {
            return MultiCheckboxComponent().apply(block)
        }
    }
}
