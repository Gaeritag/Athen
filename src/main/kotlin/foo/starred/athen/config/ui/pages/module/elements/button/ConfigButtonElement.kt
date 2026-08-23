package foo.starred.athen.config.ui.pages.module.elements.button

import foo.starred.athen.config.data.impl.ConfigButtonElementData
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.utils.brighten
import foo.starred.snowbird.utils.literal

class ConfigButtonElement : RoundedRectanglePrimitive() {
    private var action: () -> Unit = {}

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -8f, 0f)
        size = FixedSizeConstraint(100f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = Catppuccin.Mocha.Surface0.argb

        effect(OutlineEffect {
            color = Catppuccin.Mocha.Surface1.argb
            inset = false
        })

        on<MouseEvent.Press> {
            if (button != 0) return@on
            cancel()
            action()
            animateColor(Catppuccin.Mocha.Surface1.argb.brighten(0.9f), 0.15f) {
                animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
            }
        }

        on<MouseEvent.Move.Enter> {
            animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
        }

        on<MouseEvent.Move.Exit> {
            animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
        }

        adopt(text {
            wrapper = CascadeTextWrapper
            text = "Click".literal()
            textSize = 8f
            color = Catppuccin.Mocha.Text.argb
            position = CenterPositionConstraint()
        })
    }

    fun action(block: () -> Unit) {
        action = block
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigButtonElementData): ConfigButtonElement {
            return ConfigButtonElement().apply {
                action(config.function)
                attach(parent)
            }
        }
    }
}
