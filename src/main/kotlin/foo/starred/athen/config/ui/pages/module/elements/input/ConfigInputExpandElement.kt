package foo.starred.athen.config.ui.pages.module.elements.input

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigTextInputElementData
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.config.ui.pages.module.elements.input.ConfigInputElement.Companion.configInputElement
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FillSizeConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.utils.literal
import kotlin.math.min

class ConfigInputExpandElement(
    private val input: ConfigInputElement,
    private val config: ConfigTextInputElementData
) : RoundedRectanglePrimitive() {
    init {
        size = FixedSizeConstraint(14f, 14f)
        radius = RoundedRectangleRadius.of(4f)
        color = Catppuccin.Mocha.Surface0.argb
        border = true
        borderColor = Catppuccin.Mocha.Surface1.argb

        adopt(text {
            text = "⛶".literal()
            textSize = 10f
            color = Catppuccin.Mocha.Text.argb
            position = CenterPositionConstraint(1f)
            shadow = false
        })

        on<MouseEvent.Move.Enter> {
            animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
        }
        
        on<MouseEvent.Move.Exit> {
            animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
        }
        
        on<MouseEvent.Press> {
            if (button != 0) return@on
            popup()
        }
    }

    private fun popup() {
        val main = roundedRectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FillSizeConstraint()
            color = 0x80000000.toInt()

            attach(ConfigUI.scene)
        }

        val pop = roundedRectangle {
            position = CenterPositionConstraint()
            size = FixedSizeConstraint(400f, 30f)
            radius = RoundedRectangleRadius.of(8f)
            color = Catppuccin.Mocha.Base.argb
            border = true
            borderColor = Catppuccin.Mocha.Surface1.argb

            attach(main)
        }

        roundedRectangle {
            size = FixedSizeConstraint(18f, 18f)
            radius = RoundedRectangleRadius.of(4f)
            color = Catppuccin.Mocha.Surface0.argb
            border = true
            borderColor = Catppuccin.Mocha.Surface1.argb
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -4f, 0f)

            on<MouseEvent.Move.Enter> {
                animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
            }

            on<MouseEvent.Move.Exit> {
                animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
            }

            on<MouseEvent.Press> {
                parent?.parent?.detach()
                root.layout()
            }

            attach(pop)
            adopt(text {
                type = CascadeTextPrimitiveRenderer
                text = "×".literal()
                textSize = 10f
                color = Catppuccin.Mocha.Red.argb
                position = CenterPositionConstraint()
            })
        }

        configInputElement {
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 6f, 0f)
            size = FixedSizeConstraint(368f, 18f)
            placeholder = config.placeholder
            value = input.value

            on<KeyEvent.Type> {
                val value0 = value
                if (value0.length > config.max) {
                    value = value0.substring(0, config.max)
                    cursor = min(cursor, config.max)
                }

                input.value = value
                ConfigManager.update(config.key, value)
            }

            on<KeyEvent.Press> {
                input.value = value
                ConfigManager.update(config.key, value)
            }

            attach(pop)
        }
    }

    companion object {
        fun configExpandButtonElement(input: ConfigInputElement, config: ConfigTextInputElementData, block: ConfigInputExpandElement.() -> Unit): ConfigInputExpandElement {
            return ConfigInputExpandElement(input, config).apply(block)
        }
    }
}