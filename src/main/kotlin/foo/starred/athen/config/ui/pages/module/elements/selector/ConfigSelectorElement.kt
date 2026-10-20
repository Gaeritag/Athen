package foo.starred.athen.config.ui.pages.module.elements.selector

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigSelectorElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.color.brighten
import foo.starred.kontext.api.format.extension.component.literal
import kotlin.time.Duration.Companion.milliseconds

class ConfigSelectorElement(
    private val config: ConfigSelectorElementData
) : RoundedRectanglePrimitive() {
    private var value: Int = ConfigManager.get(config.key) as? Int ?: config.default
    private val text = text {
        wrapper = CascadeTextWrapper
        text = CascadeFonts.sans.truncate(config.options.getOrNull(value) ?: "Unknown", 8f, 60f, "…").literal()
        textSize = 8f
        color = CascadeGeometricColor(MochaColorScheme.Text.argb)
        position = CenterPositionConstraint()
    }

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

        adopt(image {
            location = ResourceAPI.identify("textures/gui/chevron.png")
            rotation = -90f
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(5f, 0f)
            size = FixedSizeConstraint(6f, 6f)
            interact = false
        })

        adopt(rectangle {
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(16f, 0f)
            size = FixedSizeConstraint(1f, 8f)
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            interact = false
        })

        adopt(text)
        adopt(rectangle {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(-16f, 0f)
            size = FixedSizeConstraint(1f, 8f)
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            interact = false
        })

        adopt(image {
            location = ResourceAPI.identify("textures/gui/chevron.png")
            rotation = 90f
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(-5f, 0f)
            size = FixedSizeConstraint(6f, 6f)
            interact = false
        })

        on<MouseEvent.Press> {
            if (button != InputConstants.MOUSE_BUTTON_LEFT && button != InputConstants.MOUSE_BUTTON_RIGHT) return@on
            cancel()

            val x1 = x - this@ConfigSelectorElement.x
            val direction = if (x1 < 16f) -1 else if (x1 > width - 16f) 1 else if (button == InputConstants.MOUSE_BUTTON_LEFT) 1 else -1
            val value1 = (value + direction + config.options.size) % config.options.size
            value = value1
            text.text = CascadeFonts.sans.truncate(config.options.getOrNull(value) ?: "Unknown", 8f, 60f, "…").literal()

            ConfigManager.update(config.key, value1)
            animate(::color, CascadeGeometricColor(MochaColorScheme.Surface1.argb.brighten(0.85f)), 150.milliseconds) {
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
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigSelectorElementData): ConfigSelectorElement {
            return ConfigSelectorElement(config).apply {
                attach(parent)
            }
        }
    }
}
