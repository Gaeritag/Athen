@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.render.radial.ui.components

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.events.impl.FocusEvent
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.extensions.rectangle.solid.rectangle
import foo.starred.cascade.graphics.extensions.scissor.scissor
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.snowbird.api.ZERO_PAIR
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.ctrl
import foo.starred.snowbird.api.shift
import foo.starred.snowbird.utils.withAlpha
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class RadialEditableText : IPrimitiveElement<RadialEditableText>() {
    private var initial: String = ""
    private var _cursor: Float = 0f
    private var _selection0: Float = 0f
    private var _selection1: Float = 0f

    private var commit: ((String) -> Unit)? = null

    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f
    override var color: Int = -1

    var textSize: Float = 8.5f
    var color0: Int = Mocha.Text.argb
    var color1: Int = Mocha.Lavender.argb
    var placeholder: String = "..."

    var editing: Boolean = false
        private set

    var value: String = ""
        set(v) {
            if (field == v) return
            field = v
            fn0()
        }

    var cursor: Int = 0
        set(v) {
            if (field == v) return
            field = v
            fn()
        }

    var anchor: Int = -1
        set(v) {
            if (field == v) return
            field = v
            fn()
        }

    var range: Pair<Int, Int> = ZERO_PAIR
        private set

    var selected: Boolean = false
        private set

    init {
        on<FocusEvent.Lose> {
            if (!editing) return@on
            commit()
        }

        on<MouseEvent.Press> {
            if (button != 0) return@on
            cancel()

            val bool = editing
            if (!editing) {
                initial = value
                editing = true
                root.focused = self
            }

            if (!bool) {
                cursor = value.length
                anchor = -1
                return@on
            }

            val font = CascadeFonts.arial
            val width1 = font.width(value, textSize)
            val x1 = this@RadialEditableText.x + (width - width1) / 2f
            val x2 = x.toFloat() - x1

            var i0 = 0
            var i1 = Float.MAX_VALUE

            for (i in 0..value.length) {
                val a = font.width(value.substring(0, i), textSize)
                val b = abs(a - x2)
                if (b >= i1) continue

                i1 = b
                i0 = i
            }

            anchor = if (shift) anchor.takeIf { it != -1 } ?: cursor else -1
            cursor = i0
        }

        on<KeyEvent.Press> {
            if (!editing) return@on

            val shift = shift
            val ctrl = ctrl

            when (key) {
                InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER -> {
                    commit()
                    cancel()
                    return@on
                }

                InputConstants.KEY_ESCAPE -> {
                    editing = false
                    root.focused = null
                    anchor = -1
                    value = initial

                    cancel()
                    return@on
                }

                InputConstants.KEY_LEFT -> {
                    if (shift && anchor == -1) {
                        anchor = cursor
                    }

                    if (!shift && selected) {
                        cursor = range.first
                        anchor = -1
                        cancel()
                        return@on
                    }

                    cursor = max(0, cursor - 1)
                    cancel()
                    return@on
                }

                InputConstants.KEY_RIGHT -> {
                    if (shift && anchor == -1) {
                        anchor = cursor
                    }

                    if (!shift && selected) {
                        cursor = range.second
                        anchor = -1
                        cancel()
                        return@on
                    }

                    cursor = min(value.length, cursor + 1)
                    cancel()
                    return@on
                }

                InputConstants.KEY_HOME -> {
                    if (shift && anchor == -1) {
                        anchor = cursor
                    }

                    if (!shift) {
                        anchor = -1
                    }

                    cursor = 0
                    cancel()
                    return@on
                }

                InputConstants.KEY_END -> {
                    if (shift && anchor == -1) {
                        anchor = cursor
                    }

                    if (!shift) {
                        anchor = -1
                    }

                    cursor = value.length
                    cancel()
                }

                InputConstants.KEY_BACKSPACE -> {
                    if (selected) {
                        delete()
                        cancel()
                        return@on
                    }

                    if (cursor > 0) {
                        value = value.substring(0, cursor - 1) + value.substring(cursor)
                        cursor--
                    }

                    cancel()
                }

                InputConstants.KEY_DELETE -> {
                    if (selected) {
                        delete()
                        cancel()
                        return@on
                    }

                    if (cursor < value.length) {
                        value = value.substring(0, cursor) + value.substring(cursor + 1)
                    }

                    cancel()
                }

                InputConstants.KEY_A -> {
                    if (!ctrl) return@on

                    anchor = 0
                    cursor = value.length
                    cancel()
                }

                InputConstants.KEY_C -> {
                    if (!ctrl) return@on
                    if (!selected) return@on

                    client.keyboardHandler.clipboard = value.substring(range.first, range.second)
                    cancel()
                }

                InputConstants.KEY_X -> {
                    if (!ctrl) return@on
                    if (!selected) return@on

                    client.keyboardHandler.clipboard = value.substring(range.first, range.second)
                    delete()
                    cancel()
                }

                InputConstants.KEY_V -> {
                    if (!ctrl) return@on

                    delete()
                    val clip = client.keyboardHandler.clipboard
                    value = value.substring(0, cursor) + clip + value.substring(cursor)
                    cursor += clip.length

                    cancel()
                }
            }
        }

        on<KeyEvent.Type> {
            if (!editing) return@on
            if (char.code < 32) return@on
            if (char.code == 127) return@on

            delete()
            value = value.substring(0, cursor) + char + value.substring(cursor)
            cursor++
            cancel()
        }
    }

    override fun draw(graphics: GuiGraphicsExtractor) {
        val font = CascadeFonts.arial
        graphics.scissor(x, y, width, height) {
            val text = if (value.isEmpty() && !editing) placeholder else value
            val width1 = font.width(text, textSize)
            val height1 = font.regular.height * textSize
            val x1 = x + (width - width1) / 2f
            val y1 = y + (height - textSize) / 2f

            if (editing && selected) {
                val i0 = min(_selection0, _selection1)
                val i1 = max(_selection0, _selection1)
                graphics.rectangle(x1 + i0, y1, i1 - i0, height1, Mocha.Lavender.argb.withAlpha(0.35f))
            }

            val color = if (editing) color1 else if (hovered) color1 else color0
            font.extract(graphics, text, x1, y1, color, false, textSize)

            if (editing && (System.currentTimeMillis() / 500) % 2 == 0L) {
                font.extract(graphics, "|", x1 + _cursor - 1f, y1 - 1f, Mocha.Lavender.argb, false, textSize)
            }
        }
    }

    fun commit(block: ((String) -> Unit)?) {
        commit = block
    }

    private fun fn() {
        selected = anchor != -1 && anchor != cursor
        range = if (anchor == -1) cursor to cursor else min(anchor, cursor) to max(anchor, cursor)
        fn0()
    }

    private fun fn0() {
        val font = CascadeFonts.arial
        _cursor = font.width(value.substring(0, min(cursor, value.length)), textSize)
        if (!selected) return
        _selection0 = font.width(value.substring(0, min(range.first, value.length)), textSize)
        _selection1 = font.width(value.substring(0, min(range.second, value.length)), textSize)
    }

    private fun commit() {
        editing = false
        root.focused = null
        anchor = -1
        commit?.invoke(value)
    }

    private fun delete(): Boolean {
        if (!selected) return false
        value = value.substring(0, range.first) + value.substring(range.second)
        cursor = range.first
        anchor = -1
        return true
    }

    companion object {
        inline fun radialEditableText(block: RadialEditableText.() -> Unit): RadialEditableText {
            return RadialEditableText().apply(block)
        }
    }
}
