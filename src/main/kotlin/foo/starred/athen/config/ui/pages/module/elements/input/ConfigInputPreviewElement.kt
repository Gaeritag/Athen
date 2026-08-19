package foo.starred.athen.config.ui.pages.module.elements.input

import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.base.IPositionConstraint
import foo.starred.cascade.constraints.base.ISizeConstraint
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.literal
import net.minecraft.client.gui.GuiGraphicsExtractor

class ConfigInputPreviewElement(private val value: () -> String) : RoundedRectanglePrimitive() {
    private val text = text {
        type = CascadeTextPrimitiveRenderer
        textSize = 10f
        color = Catppuccin.Mocha.Text.argb
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.START, 4f, 4f)
    }

    private val tooltip = object : RoundedRectanglePrimitive() {
        override fun render(graphics: GuiGraphicsExtractor) {
            if (!visible) return

            graphics.nextStratum()
            super.render(graphics)
        }
    }.apply {
        color = Catppuccin.Mocha.Base.argb
        border = true
        borderColor = Catppuccin.Mocha.Surface1.argb
        radius = RoundedRectangleRadius.of(4f)
        visible = false
        interact = false

        size = object : ISizeConstraint {
            override fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.width + 8f
            override fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.height + 8f
        }

        adopt(text)
    }

    init {
        size = FixedSizeConstraint(14f, 14f)
        radius = RoundedRectangleRadius.of(4f)
        color = Catppuccin.Mocha.Surface0.argb
        border = true
        borderColor = Catppuccin.Mocha.Surface1.argb

        adopt(text {
            type = CascadeTextPrimitiveRenderer
            text = "?".literal()
            textSize = 10f
            color = Catppuccin.Mocha.Text.argb
            position = CenterPositionConstraint()
        })

        on<MouseEvent.Move.Any> {
            if (!hovered) return@on

            tooltip.position = object : IPositionConstraint {
                override fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = x.toFloat() + 5f
                override fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = y.toFloat() + 5f
            }
        }

        on<MouseEvent.Move.Enter> {
            tooltip.visible = true
            text.text = value().parse()
            animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
        }

        on<MouseEvent.Move.Exit> {
            tooltip.visible = false
            animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
        }

        tooltip.attach(ConfigUI.scene)
    }

    companion object {
        fun configPreviewButtonElement(value: () -> String, block: ConfigInputPreviewElement.() -> Unit): ConfigInputPreviewElement {
            return ConfigInputPreviewElement(value).apply(block)
        }
    }
}
