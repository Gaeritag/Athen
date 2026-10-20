package foo.starred.athen.config.ui.pages.module.elements.input

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigTextInputElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.config.ui.pages.module.elements.input.ConfigInputElement.Companion.configInputElement
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FillSizeConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.component.literal
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

class ConfigInputExpandElement(
    private val input: ConfigInputElement,
    private val config: ConfigTextInputElementData
) : RoundedRectanglePrimitive() {
    init {
        size = FixedSizeConstraint(14f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor.TRANSPARENT

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        adopt(text {
            wrapper = CascadeTextWrapper
            text = "⛶".literal()
            textSize = 10f
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
            position = CenterPositionConstraint()
            shadow = false
        })

        on<MouseEvent.Move.Enter> {
            animate(150.milliseconds) {
                effect<OutlineEffect> {
                    ::color to CascadeGeometricColor(MochaColorScheme.Lavender.argb)
                }
            }
        }

        on<MouseEvent.Move.Exit> {
            animate(150.milliseconds) {
                effect<OutlineEffect> {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                }
            }
        }

        on<MouseEvent.Press> {
            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
            popup()
        }
    }

    private fun popup() {
        val main = roundedRectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FillSizeConstraint()
            color = CascadeGeometricColor(MochaColorScheme.Crust.alpha(0.6f))

            on<MouseEvent.Press> {
                detach()
            }

            attach(ConfigUI.scene)
        }

        val pop = roundedRectangle {
            position = CenterPositionConstraint()
            size = FixedSizeConstraint(404f, 30f)
            radius = CascadeGeometricRadius(8f)
            color = CascadeGeometricColor(MochaColorScheme.Base.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                inset = false
            })

            on<MouseEvent.Press> {
                cancel()
            }

            attach(main)
        }

        roundedRectangle {
            size = FixedSizeConstraint(18f, 18f)
            radius = CascadeGeometricRadius(4f)
            color = CascadeGeometricColor.TRANSPARENT
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(-6f, 0f)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                inset = false
            })

            on<MouseEvent.Move.Enter> {
                animate(150.milliseconds) {
                    effect<OutlineEffect> {
                        ::color to CascadeGeometricColor(MochaColorScheme.Red.argb)
                    }
                }
            }

            on<MouseEvent.Move.Exit> {
                animate(150.milliseconds) {
                    effect<OutlineEffect> {
                        ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                    }
                }
            }

            on<MouseEvent.Press> {
                main.detach()
            }

            attach(pop)
            adopt(text {
                wrapper = CascadeTextWrapper
                text = "×".literal()
                textSize = 10f
                color = CascadeGeometricColor(MochaColorScheme.Red.argb)
                position = CenterPositionConstraint()
            })
        }

        configInputElement {
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(6f, 0f)
            size = FixedSizeConstraint(368f, 18f)
            placeholder = config.placeholder
            value = input.value

            on<KeyEvent.Type> {
                val value0 = value
                if (value0.length > config.max) {
                    value = value0.substring(0, config.max)
                    block = min(block, config.max)
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
