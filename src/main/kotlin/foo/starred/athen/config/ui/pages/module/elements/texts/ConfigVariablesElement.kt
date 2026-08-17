package foo.starred.athen.config.ui.pages.module.elements.texts

import foo.starred.athen.config.data.impl.ConfigVariablesElementData
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.font.CascadeFonts
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.parser.parse

class ConfigVariablesElement(
    config: ConfigVariablesElementData
) : ContainerPrimitive() {
    init {
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
        size = FixedSizeConstraint(482f, 26f)

        val text0 = "<gray>${config.name}:".parse()
        adopt(text {
            type = CascadeTextPrimitiveRenderer
            text = text0
            textSize = 10f
            color = Catppuccin.Mocha.Subtext0.argb
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 10f, 0f)
        })

        val start = 10f + CascadeFonts.arial.width(text0, 10f) + 8f
        var last: IPrimitiveElement<*>? = null

        for (token in config.tokens) {
            val last0 = last
            val parsed = token.parse()

            last = roundedRectangle {
                position = if (last0 == null) AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, start, 0f) else AnchorPositionConstraint({ last0 }, PositionAnchor.RIGHT, 4f, 0f)
                size = FixedSizeConstraint(CascadeFonts.arial.width(parsed, 9.5f) + 10f, 16f)
                radius = RoundedRectangleRadius.of(3f)
                color = Catppuccin.Mocha.Surface0.argb
                border = true
                borderColor = Catppuccin.Mocha.Surface1.argb
                borderInset = false

                on<MouseEvent.Move.Enter> {
                    animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
                }

                on<MouseEvent.Move.Any> {
                    if (!hovered) return@on
                    ConfigUI.show("Click to copy <#CBA6F7>$token", x, y)
                }

                on<MouseEvent.Move.Exit> {
                    animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
                    ConfigUI.hide()
                }

                on<MouseEvent.Press> {
                    if (button != 0) return@on
                    cancel()
                    client.keyboardHandler.clipboard = token
                    ConfigUI.show("<#A6E3A1>Copied $token to clipboard!", x, y)
                    animateColor(Catppuccin.Mocha.Green.argb, 0.1f) {
                        animateColor(Catppuccin.Mocha.Surface1.argb, 0.2f)
                    }
                }

                adopt(text {
                    type = CascadeTextPrimitiveRenderer
                    text = parsed
                    textSize = 9.5f
                    color = Catppuccin.Mocha.Lavender.argb
                    position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.CENTER)
                })

                attach(this@ConfigVariablesElement)
            }
        }
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigVariablesElementData): ConfigVariablesElement {
            return ConfigVariablesElement(config).apply {
                attach(parent)
            }
        }
    }
}