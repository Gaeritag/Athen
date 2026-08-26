package foo.starred.athen.config.ui.pages.module.elements.hud

import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigHudElementData
import foo.starred.athen.config.ui.pages.module.elements.toggle.ConfigSwitchElement
import foo.starred.athen.hud.HUDEditor
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.snowbird.utils.open

class ConfigHUDElement(
    private val config: ConfigHudElementData
) : ContainerPrimitive() {
    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -8f, 0f)
        size = FixedSizeConstraint(46f, 14f)
        interact = false

        adopt(roundedRectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FixedSizeConstraint(14f, 14f)
            radius = CascadeGeometricRadius(4f)
            color = Catppuccin.Mocha.Surface0.argb

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface1.argb
                inset = false
            })

            on<MouseEvent.Press> {
                if (button != 0) return@on
                cancel()
                HUDEditor.open()
            }

            on<MouseEvent.Move.Enter> {
                animateColor(Catppuccin.Mocha.Surface1.argb, 0.15f)
            }

            on<MouseEvent.Move.Exit> {
                animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
            }

            adopt(image {
                location = ResourceAPI.identify("textures/gui/move.png")
                color = Catppuccin.Mocha.Subtext0.argb
                position = CenterPositionConstraint()
                size = FixedSizeConstraint(12f, 12f)
                interact = false
            })
        })

        adopt(ConfigSwitchElement().apply {
            position = FixedPositionConstraint(18f, 0f)

            val bool = ConfigManager.get(config.key) as? Boolean ?: config.default
            config.hud.enabled = bool
            set(bool, false)

            update {
                config.hud.enabled = it
                ConfigManager.update(config.key, it)
            }
        })
    }

    companion object {
        fun of(parent: IPrimitiveElement<*>, config: ConfigHudElementData): ConfigHUDElement {
            return ConfigHUDElement(config).apply {
                attach(parent)
            }
        }
    }
}
