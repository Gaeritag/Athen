package foo.starred.athen.config.module.irc

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.modules.impl.general.IRC
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.graphics.geometry.CascadeGeometricResolution
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.screen.CascadeScreen
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.text.parser.impl.parse
import kotlin.time.Duration.Companion.milliseconds

object ConfigIrcUI : CascadeScreen("IRC [Athen]", CascadeGeometricResolution.FHD.of(2f)) {
    private var header: TextPrimitive
    private var button: RoundedRectanglePrimitive
    private var text: TextPrimitive

    init {
        val window = roundedRectangle {
            position = CenterPositionConstraint()
            size = FixedSizeConstraint(200f, 82f)
            radius = CascadeGeometricRadius(5f)
            color = CascadeGeometricColor(MochaColorScheme.Base.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                inset = false
            })

            attach(scene)
        }

        header = text {
            wrapper = CascadeTextWrapper
            textSize = 10.5f
            color = CascadeGeometricColor(MochaColorScheme.Text.argb)
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
            offset = CascadeGeometricOffset(0f, 11f)
            attach(window)
        }

        text {
            wrapper = CascadeTextWrapper
            text = "Toggle Athen's in-game chat".literal()
            textSize = 8f
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.alpha(0.80f))
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
            offset = CascadeGeometricOffset(0f, 25f)

            attach(window)
        }

        rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 1))
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
            offset = CascadeGeometricOffset(0f, 41f)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

            attach(window)
        }

        button = roundedRectangle {
            size = FixedSizeConstraint(80f, 21f)
            radius = CascadeGeometricRadius(4f)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.END)
            offset = CascadeGeometricOffset(14f, -10f)

            effect(OutlineEffect {
                inset = false
            })

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                ConfigManager.update(IRC.config.key, !IRC.enabled)
                update()
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                animate(120.milliseconds) {
                    ::color to CascadeGeometricColor(if (IRC.enabled) MochaColorScheme.Red.alpha(0.30f) else MochaColorScheme.Green.alpha(0.30f))
                }
            }

            on<MouseEvent.Move.Exit> {
                animate(120.milliseconds) {
                    ::color to CascadeGeometricColor(if (IRC.enabled) MochaColorScheme.Red.alpha(0.18f) else MochaColorScheme.Green.alpha(0.18f))
                }
            }

            attach(window)
        }

        text = text {
            wrapper = CascadeTextWrapper
            textSize = 8.5f
            position = CenterPositionConstraint()
            attach(button)
        }

        roundedRectangle {
            size = FixedSizeConstraint(80f, 21f)
            radius = CascadeGeometricRadius(4f)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.END)
            offset = CascadeGeometricOffset(-14f, -10f)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                inset = false
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "Close".literal()
                textSize = 8.5f
                color = CascadeGeometricColor(MochaColorScheme.Text.argb)
                position = CenterPositionConstraint()
            })

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                cancel()
                onClose()
            }

            on<MouseEvent.Move.Enter> {
                animate(120.milliseconds) {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                }
            }

            on<MouseEvent.Move.Exit> {
                animate(120.milliseconds) {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                }
            }

            attach(window)
        }

        update()
    }

    private fun update() {
        header.text = if (IRC.enabled) "Athen IRC <gray>[<green>Enabled<gray>]".parse() else "Athen IRC <gray>[<red>Disabled<gray>]".parse()

        if (IRC.enabled) {
            button.color = CascadeGeometricColor(MochaColorScheme.Red.alpha(0.18f))
            button.effect<OutlineEffect>()?.color = CascadeGeometricColor(MochaColorScheme.Red.alpha(0.50f))
            text.color = CascadeGeometricColor(MochaColorScheme.Red.argb)
            text.text = "Disable".literal()
            return
        }

        button.color = CascadeGeometricColor(MochaColorScheme.Green.alpha(0.18f))
        button.effect<OutlineEffect>()?.color = CascadeGeometricColor(MochaColorScheme.Green.alpha(0.50f))
        text.color = CascadeGeometricColor(MochaColorScheme.Green.argb)
        text.text = "Enable".literal()
    }
}
