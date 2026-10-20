package foo.starred.athen.api.rendering.ui.components.impl

import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.extensions.rectangle.hollow.hollowRectangle
import foo.starred.cascade.graphics.extensions.rectangle.solid.rectangle
import foo.starred.cascade.graphics.extensions.scissor.scissor
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.max
import kotlin.math.min

open class MultiCheckboxComponent : IPrimitiveElement<MultiCheckboxComponent>() {
    private var selected: (Int) -> Boolean = { false }
    private var select: (Int) -> Unit = {}
    private var _height1: Int? = null

    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f
    override var color: CascadeGeometricColor = CascadeGeometricColor.WHITE

    var label: String = ""
    var text: String = ""
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
                if (i in items.indices) select(i)
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

    override fun draw(graphics: GuiGraphicsExtractor) {
        if (root.focused != this) open = false

        if (label.isNotEmpty()) MinecraftFontRenderer.extract(graphics, label, x, y - MinecraftFontMeasurer.height - 2, false, MochaColorScheme.Subtext0.argb)
        graphics.rectangle(x, y, width, height, MochaColorScheme.Surface1.argb)
        graphics.hollowRectangle(x, y, width, height, 1f, if (open) MochaColorScheme.Lavender.argb else MochaColorScheme.Surface2.argb, inset = false)

        MinecraftFontRenderer.extract(graphics, text, x + 4, y + (height - MinecraftFontMeasurer.height) / 2 + 1, false, MochaColorScheme.Text.argb)
        MinecraftFontRenderer.extract(graphics, if (open) "▾" else "▸", x + width - 12, y + (height - MinecraftFontMeasurer.height) / 2 + 1, false, MochaColorScheme.Overlay0.argb)

        if (open) {
            val height1 = height1.toFloat()

            graphics.rectangle(x, y + height, width, height1, MochaColorScheme.Base.argb)
            graphics.hollowRectangle(x, y + height, width, height1, 1f, MochaColorScheme.Lavender.argb, inset = false)

            graphics.scissor(x, y + height + 1, width, height + height1 - 1) {
                var y0 = y + height - scroll
                for ((idx, item) in items.withIndex()) {
                    if (y0 + 14 <= y + height || y0 >= y + height + height1) {
                        y0 += 14
                        continue
                    }

                    graphics.rectangle(x, y0, width, 14f, MochaColorScheme.Base.argb)

                    val b = selected(idx)
                    MinecraftFontRenderer.extract(graphics, item, x + 4, y0 + (14 - MinecraftFontMeasurer.height) / 2 + 1, false, if (b) MochaColorScheme.Lavender.argb else MochaColorScheme.Text.argb)
                    if (b) MinecraftFontRenderer.extract(graphics, "✔", x + width - 14, y0 + (14 - MinecraftFontMeasurer.height) / 2 + 1, false, MochaColorScheme.Lavender.argb)

                    y0 += 14
                }
            }
        }
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

    fun selected(block: (Int) -> Boolean) {
        selected = block
    }

    fun select(block: (Int) -> Unit) {
        select = block
    }

    companion object {
        inline fun multiCheckbox(block: MultiCheckboxComponent.() -> Unit): MultiCheckboxComponent {
            return MultiCheckboxComponent().apply(block)
        }
    }
}
