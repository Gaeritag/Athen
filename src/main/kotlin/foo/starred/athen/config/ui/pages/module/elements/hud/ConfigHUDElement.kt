package foo.starred.athen.config.ui.pages.module.elements.hud

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigHudElementData
import foo.starred.athen.config.hud.ui.HudElementEditorUI
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.config.ui.pages.module.elements.toggle.ConfigSwitchElement
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.kontext.abstraction.gui.MinecraftGui
import kotlin.time.Duration.Companion.milliseconds

class ConfigHUDElement(
    private val config: ConfigHudElementData
) : ContainerPrimitive() {
    init {
        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
        offset = CascadeGeometricOffset(-8f, 0f)
        size = FixedSizeConstraint(46f, 14f)
        interact = false

        adopt(roundedRectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FixedSizeConstraint(14f, 14f)
            radius = CascadeGeometricRadius(4f)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                inset = false
            })

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
                cancel()
                MinecraftGui.screen(HudElementEditorUI)
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

            adopt(image {
                location = ResourceAPI.identify("textures/gui/move.png")
                color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
                position = CenterPositionConstraint()
                size = FixedSizeConstraint(12f, 12f)
                interact = false
            })
        })

        adopt(ConfigSwitchElement().apply {
            position = FixedPositionConstraint(18f, 0f)
            offset = CascadeGeometricOffset.ZERO

            val bool = ConfigManager.get(config.key) as? Boolean ?: config.default
            config.hud.state.value = bool
            set(bool, false)

            update {
                config.hud.state.value = it
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
