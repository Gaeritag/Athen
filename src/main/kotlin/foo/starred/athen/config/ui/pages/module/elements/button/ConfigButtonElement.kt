package foo.starred.athen.config.ui.pages.module.elements.button

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.data.impl.ConfigButtonElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.color.brighten
import foo.starred.kontext.api.format.extension.component.literal
import kotlin.time.Duration.Companion.milliseconds

class ConfigButtonElement : RoundedRectanglePrimitive() {
    private var action: () -> Unit = {}

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
        offset = CascadeGeometricOffset(-8f, 0f)
        size = FixedSizeConstraint(100f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        on<MouseEvent.Press> {
            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

            cancel()
            action()

            animate(::color, CascadeGeometricColor(MochaColorScheme.Surface1.argb.brighten(0.9f)), 150.milliseconds) {
                animate(150.milliseconds) {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                }
            }
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

        adopt(text {
            wrapper = CascadeTextWrapper
            text = "Click".literal()
            textSize = 8f
            color = CascadeGeometricColor(MochaColorScheme.Text.argb)
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
