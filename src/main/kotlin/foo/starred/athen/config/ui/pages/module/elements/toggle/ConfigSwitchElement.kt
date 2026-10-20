package foo.starred.athen.config.ui.pages.module.elements.toggle

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigSwitchElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.animation.interpolator.easing.impl.EaseOutEasingInterpolator
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.kontext.api.format.extension.color.brighten
import kotlin.time.Duration.Companion.milliseconds

class ConfigSwitchElement : RoundedRectanglePrimitive() {
    private var update: (Boolean) -> Unit = {}
    private var active: Boolean = false

    private val knob = roundedRectangle {
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
        size = FixedSizeConstraint(10f, 10f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor(MochaColorScheme.Text.argb)
        offset = CascadeGeometricOffset(2f, 0f)
        interact = false
    }

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
        offset = CascadeGeometricOffset(-8f, 0f)
        size = FixedSizeConstraint(28f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        on<MouseEvent.Press> {
            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
            cancel()

            set(!active)
            update(active)
        }

        on<MouseEvent.Move.Enter> {
            if (active) return@on

            animate(150.milliseconds) {
                ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            }
        }

        on<MouseEvent.Move.Exit> {
            if (active) return@on

            animate(150.milliseconds) {
                ::color to CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            }
        }

        adopt(knob)
    }

    fun set(state: Boolean, animated: Boolean = true) {
        if (active == state) return
        active = state

        val color1 = CascadeGeometricColor(if (active) MochaColorScheme.Lavender.argb.brighten(0.75f) else MochaColorScheme.Surface0.argb)
        val offset1 = if (active) CascadeGeometricOffset(16f, 0f) else CascadeGeometricOffset(2f, 0f)

        if (animated) {
            animate(250.milliseconds, EaseOutEasingInterpolator) {
                ::color to color1
                knob::offset to offset1
            }

            return
        }

        color = color1
        knob.offset = offset1
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
