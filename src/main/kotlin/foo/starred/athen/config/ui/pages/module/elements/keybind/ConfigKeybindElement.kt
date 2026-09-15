package foo.starred.athen.config.ui.pages.module.elements.keybind

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigKeybindElementData
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.FocusEvent
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.api.inputs.impl.GenericInputState
import foo.starred.snowbird.api.inputs.impl.KeyboardInputState
import foo.starred.snowbird.api.inputs.impl.MouseInputState
import foo.starred.snowbird.utils.literal

class ConfigKeybindElement(
    private val config: ConfigKeybindElementData
) : ContainerPrimitive() {
    private lateinit var outline: OutlineEffect

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
        color = CascadeGeometricColor(Catppuccin.Mocha.Text.argb)
        position = CenterPositionConstraint()
    }

    private val main = roundedRectangle {
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 0f, 0f)
        size = FixedSizeConstraint(82f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor(Catppuccin.Mocha.Surface0.argb)

        effect(OutlineEffect {
            color = CascadeGeometricColor(Catppuccin.Mocha.Surface1.argb)
            inset = false
        }.also { outline = it })

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
            animateColor(CascadeGeometricColor(Catppuccin.Mocha.Surface1.argb), 0.15f)
        }

        on<MouseEvent.Move.Exit> {
            if (listening) return@on
            animateColor(CascadeGeometricColor(Catppuccin.Mocha.Surface0.argb), 0.15f)
        }

        adopt(key)
    }

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -8f, 0f)
        size = FixedSizeConstraint(100f, 14f)

        adopt(main)
        adopt(roundedRectangle {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, 0f, 0f)
            size = FixedSizeConstraint(14f, 14f)
            radius = CascadeGeometricRadius(4f)
            color = CascadeGeometricColor(Catppuccin.Mocha.Surface0.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(Catppuccin.Mocha.Surface1.argb)
                inset = false
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "×".literal()
                textSize = 8f
                color = CascadeGeometricColor(Catppuccin.Mocha.Subtext0.argb)
                position = CenterPositionConstraint()
            })

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                update(InputConstants.UNKNOWN)
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                animateColor(CascadeGeometricColor(Catppuccin.Mocha.Surface1.argb), 0.15f)
            }

            on<MouseEvent.Move.Exit> {
                animateColor(CascadeGeometricColor(Catppuccin.Mocha.Surface0.argb), 0.15f)
            }
        })
    }

    private fun start() {
        listening = true
        root.focused = main
        key.text = "...".literal()
        key.color = CascadeGeometricColor(Catppuccin.Mocha.Crust.argb)

        main.animateColor(CascadeGeometricColor(Catppuccin.Mocha.Peach.argb), 0.15f)
        outline.color = CascadeGeometricColor(Catppuccin.Mocha.Peach.argb)
    }

    private fun stop() {
        listening = false
        if (root.focused == main) root.focused = null
        key.text = GenericInputState.name(value).literal()

        key.color = CascadeGeometricColor(Catppuccin.Mocha.Text.argb)
        main.animateColor(CascadeGeometricColor(if (main.hovered) Catppuccin.Mocha.Surface1.argb else Catppuccin.Mocha.Surface0.argb), 0.15f)
        outline.color = CascadeGeometricColor(Catppuccin.Mocha.Surface1.argb)
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
