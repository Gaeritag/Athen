package foo.starred.athen.config.ui.pages.module.elements.keybind

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigKeybindElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.FocusEvent
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.abstraction.input.impl.GenericInputState
import foo.starred.kontext.abstraction.input.impl.KeyboardInputState
import foo.starred.kontext.abstraction.input.impl.MouseInputState
import foo.starred.kontext.api.format.extension.component.literal
import kotlin.time.Duration.Companion.milliseconds

class ConfigKeybindElement(
    private val config: ConfigKeybindElementData
) : ContainerPrimitive() {
    private var listening = false
    private var value: InputConstants.Key = when (val v = ConfigManager.get(config.key)) {
        is InputConstants.Key -> v
        is String -> runCatching { InputConstants.getKey(v) }.getOrNull()
        else -> null
    } ?: config.default

    private val key = text {
        wrapper = CascadeTextWrapper
        text = GenericInputState.name(value).literal()
        textSize = 8f
        color = CascadeGeometricColor(MochaColorScheme.Text.argb)
        position = CenterPositionConstraint()
    }

    private val main = roundedRectangle {
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
        size = FixedSizeConstraint(82f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        on<MouseEvent.Press> {
            if (listening) update(MouseInputState.vanilla(button)) else start()
            cancel()
        }

        on<KeyEvent.Press> {
            if (!listening) return@on
            cancel()

            when (key) {
                InputConstants.KEY_ESCAPE -> stop()
                InputConstants.KEY_BACKSPACE, InputConstants.KEY_DELETE -> update(InputConstants.UNKNOWN)
                //~ if >= 26.3 'KEYSYM' -> 'KEYBOARD'
                else -> update(KeyboardInputState.vanilla(key))
            }
        }

        on<FocusEvent.Lose> {
            stop()
        }

        on<MouseEvent.Move.Enter> {
            if (listening) return@on
            animate(150.milliseconds) {
                ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            }
        }

        on<MouseEvent.Move.Exit> {
            if (listening) return@on
            animate(150.milliseconds) {
                ::color to CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            }
        }

        adopt(key)
    }

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
        offset = CascadeGeometricOffset(-8f, 0f)
        size = FixedSizeConstraint(100f, 14f)

        adopt(main)
        adopt(roundedRectangle {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            size = FixedSizeConstraint(14f, 14f)
            radius = CascadeGeometricRadius(4f)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                inset = false
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "×".literal()
                textSize = 8f
                color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
                position = CenterPositionConstraint()
            })

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                update(InputConstants.UNKNOWN)
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                animate(150.milliseconds) {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                }
            }

            on<MouseEvent.Move.Exit> {
                animate(150.milliseconds) {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                }
            }
        })
    }

    private fun start() {
        listening = true
        root.focused = main
        key.text = "...".literal()
        key.color = CascadeGeometricColor(MochaColorScheme.Crust.argb)

        main.effect<OutlineEffect>()?.color = CascadeGeometricColor(MochaColorScheme.Peach.argb)
        main.animate(150.milliseconds) {
            ::color to CascadeGeometricColor(MochaColorScheme.Peach.argb)
        }
    }

    private fun stop() {
        listening = false
        if (root.focused == main) root.focused = null
        key.text = GenericInputState.name(value).literal()
        key.color = CascadeGeometricColor(MochaColorScheme.Text.argb)

        main.effect<OutlineEffect>()?.color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
        main.animate(150.milliseconds) {
            ::color to CascadeGeometricColor(if (main.hovered) MochaColorScheme.Surface1.argb else MochaColorScheme.Surface0.argb)
        }
    }

    private fun update(key1: InputConstants.Key) {
        value = key1
        ConfigManager.update(config.key, value)
        stop()
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigKeybindElementData): ConfigKeybindElement {
            return ConfigKeybindElement(config).apply {
                attach(parent)
            }
        }
    }
}
