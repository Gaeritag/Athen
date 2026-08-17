package foo.starred.athen.config.ui.pages.module.elements.selector

import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigSelectorElementData
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.font.CascadeFonts
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.utils.brighten
import foo.starred.snowbird.utils.literal

class ConfigSelectorElement(
    private val config: ConfigSelectorElementData
) : RoundedRectanglePrimitive() {
    private var value: Int = ConfigManager.get(config.key) as? Int ?: config.default
    private val text = text {
        type = CascadeTextPrimitiveRenderer
        text = CascadeFonts.arial.truncate(config.options.getOrNull(value) ?: "Unknown", 8f, 60f, "…").literal()
        textSize = 8f
        color = Catppuccin.Mocha.Text.argb
        position = CenterPositionConstraint()
    }

    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -8f, 0f)
        size = FixedSizeConstraint(100f, 14f)
        radius = RoundedRectangleRadius.of(4f)
        color = Catppuccin.Mocha.Surface0.argb
        border = true
        borderColor = Catppuccin.Mocha.Surface1.argb
        borderInset = false

        adopt(image {
            location = ResourceAPI.identify("textures/gui/chevron.png")
            rotation = -90f
            color = Catppuccin.Mocha.Subtext0.argb
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 5f, 0f)
            size = FixedSizeConstraint(6f, 6f)
            interact = false
        })

        adopt(rectangle {
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 16f, 0f)
            size = FixedSizeConstraint(1f, 8f)
            color = Catppuccin.Mocha.Surface2.argb
            interact = false
        })

        adopt(text)

        adopt(rectangle {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -16f, 0f)
            size = FixedSizeConstraint(1f, 8f)
            color = Catppuccin.Mocha.Surface2.argb
            interact = false
        })

        adopt(image {
            location = ResourceAPI.identify("textures/gui/chevron.png")
            rotation = 90f
            color = Catppuccin.Mocha.Subtext0.argb
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -5f, 0f)
            size = FixedSizeConstraint(6f, 6f)
            interact = false
        })

        on<MouseEvent.Press> {
            if (button != 0 && button != 1) return@on
            cancel()

            val x1 = x - this@ConfigSelectorElement.x
            val direction = if (x1 < 16f) -1 else if (x1 > width - 16f) 1 else if (button == 0) 1 else -1
            val value1 = (value + direction + config.options.size) % config.options.size
            value = value1
            text.text = CascadeFonts.arial.truncate(config.options.getOrNull(value) ?: "Unknown", 8f, 60f, "…").literal()

            ConfigManager.update(config.key, value1)
            animateColor(Catppuccin.Mocha.Surface1.argb.brighten(0.9f), 0.15f) {
                animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
            }
        }

        on<MouseEvent.Move.Enter> {
            animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
        }

        on<MouseEvent.Move.Exit> {
            animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
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