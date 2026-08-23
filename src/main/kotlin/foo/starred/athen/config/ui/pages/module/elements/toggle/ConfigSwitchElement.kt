package foo.starred.athen.config.ui.pages.module.elements.toggle

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigSwitchElementData
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.animation.data.AnimatableFloat
import foo.starred.cascade.animation.enums.CascadeAnimations
import foo.starred.cascade.constraints.base.IPositionConstraint
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.snowbird.utils.brighten

class ConfigSwitchElement : RoundedRectanglePrimitive() {
    private val knob = AnimatableFloat(2f)
    private var update: (Boolean) -> Unit = {}
    private var active: Boolean = false

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -8f, 0f)
        size = FixedSizeConstraint(28f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = Catppuccin.Mocha.Surface0.argb

        effect(OutlineEffect {
            color = Catppuccin.Mocha.Surface1.argb
            inset = false
        })

        on<MouseEvent.Press> {
            if (button != 0) return@on
            cancel()

            set(!active)
            update(active)
        }

        on<MouseEvent.Move.Enter> {
            if (active) return@on
            animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
        }

        on<MouseEvent.Move.Exit> {
            if (active) return@on
            animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
        }

        adopt(roundedRectangle {
            position = object : IPositionConstraint {
                override fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = parent.x + knob.value
                override fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = parent.y + 2f
            }
            size = FixedSizeConstraint(10f, 10f)
            radius = CascadeGeometricRadius(4f)
            color = Catppuccin.Mocha.Text.argb
            interact = false
        })
    }

    fun set(state: Boolean, animated: Boolean = true) {
        if (active == state) return
        active = state

        if (animated) {
            animateColor(if (active) Catppuccin.Mocha.Lavender.argb.brighten(0.75f) else Catppuccin.Mocha.Surface0.argb, 0.25f, CascadeAnimations.EASE_OUT)
            val manager = root.animations
            if (manager != null) knob.animate(manager, if (active) 16f else 2f, 0.25f, CascadeAnimations.EASE_OUT) else knob.snap(if (active) 16f else 2f)
            return
        }

        color = if (active) Catppuccin.Mocha.Lavender.argb.brighten(0.75f) else Catppuccin.Mocha.Surface0.argb
        knob.snap(if (active) 16f else 2f)
    }

    fun update(block: (Boolean) -> Unit) {
        update = block
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigSwitchElementData): ConfigSwitchElement {
            return ConfigSwitchElement().apply {
                attach(parent)
                set(ConfigManager.get(config.key) as? Boolean ?: config.default, false)

                update {
                    ConfigManager.update(config.key, it)
                }
            }
        }
    }
}