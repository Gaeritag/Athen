package foo.starred.athen.config.ui.pages.module

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.data.feature.ConfigFeatureData
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.config.ui.pages.main.ConfigCategories
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
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.text.parser.impl.parse
import kotlin.time.Duration.Companion.milliseconds

object ConfigModulesNavigationState {
    private data class State(val category: ConfigCategory, val module: ConfigFeatureData?)
    private val back = mutableListOf<State>()
    private val forward = mutableListOf<State>()

    fun fn(): IPrimitiveElement<*> {
        return roundedRectangle {
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(-8f, 0f)
            size = FixedSizeConstraint(68f, 18f)
            color = CascadeGeometricColor(MochaColorScheme.Surface0.alpha(0.45f))
            radius = CascadeGeometricRadius(4f)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.30f))
                inset = false
            })

            adopt(roundedRectangle {
                position = FixedPositionConstraint(0f, 0f)
                size = FixedSizeConstraint(22f, 18f)
                radius = CascadeGeometricRadius(4f, 0f, 4f, 0f)
                color = CascadeGeometricColor.TRANSPARENT

                adopt(image {
                    location = ResourceAPI.identify("textures/gui/chevron.png")
                    rotation = -90f
                    color = CascadeGeometricColor(MochaColorScheme.Lavender.argb)
                    position = CenterPositionConstraint()
                    size = FixedSizeConstraint(6f, 6f)
                    interact = false
                })

                on<MouseEvent.Press> {
                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                    cancel()
                    back()
                }

                on<MouseEvent.Move.Enter> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.30f))
                    }
                }

                on<MouseEvent.Move.Exit> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor.TRANSPARENT
                    }
                }
            })

            adopt(rectangle {
                position = FixedPositionConstraint(22f, 0f)
                size = FixedSizeConstraint(1f, 18f)
                color = CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.25f))
                interact = false
            })

            adopt(roundedRectangle {
                position = FixedPositionConstraint(23f, 0f)
                size = FixedSizeConstraint(22f, 18f)
                radius = CascadeGeometricRadius.ZERO
                color = CascadeGeometricColor.TRANSPARENT

                adopt(image {
                    location = ResourceAPI.identify("textures/gui/chevron.png")
                    rotation = 90f
                    color = CascadeGeometricColor(MochaColorScheme.Lavender.argb)
                    position = CenterPositionConstraint()
                    size = FixedSizeConstraint(6f, 6f)
                    interact = false
                })

                on<MouseEvent.Press> {
                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                    cancel()
                    forward()
                }

                on<MouseEvent.Move.Enter> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.30f))
                    }
                }

                on<MouseEvent.Move.Exit> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor.TRANSPARENT
                    }
                }
            })

            adopt(rectangle {
                position = FixedPositionConstraint(45f, 0f)
                size = FixedSizeConstraint(1f, 18f)
                color = CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.25f))
                interact = false
            })

            adopt(roundedRectangle {
                position = FixedPositionConstraint(46f, 0f)
                size = FixedSizeConstraint(22f, 18f)
                radius = CascadeGeometricRadius(0f, 4f, 0f, 4f)
                color = CascadeGeometricColor.TRANSPARENT

                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = "<${MochaColorScheme.Lavender.argb}>×".parse()
                    textSize = 14f
                    position = CenterPositionConstraint()
                    offset = CascadeGeometricOffset(0f, -0.5f)
                })

                on<MouseEvent.Press> {
                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                    cancel()
                    ConfigUI.onClose()
                }

                on<MouseEvent.Move.Enter> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor(MochaColorScheme.Red.alpha(0.40f))
                    }
                }

                on<MouseEvent.Move.Exit> {
                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor.TRANSPARENT
                    }
                }
            })
        }
    }

    fun navigate(category: ConfigCategory, module: ConfigFeatureData?, record: Boolean = true) {
        val current = State(ConfigCategories.active, ConfigModules.active)
        val target = State(category, module)
        if (current == target) return

        if (record) {
            back.push(current)
            forward.clear()
        }

        ConfigCategories.active = category
        ConfigModules.active = module
        ConfigCategories.fn()
        ConfigModules.fn()
    }

    fun back() {
        if (back.isNotEmpty()) {
            forward.push(State(ConfigCategories.active, ConfigModules.active))

            val last = back.removeLast()
            ConfigCategories.active = last.category
            ConfigModules.active = last.module
            ConfigCategories.fn()
            ConfigModules.fn()
            return
        }

        if (ConfigModules.active != null) {
            forward.push(State(ConfigCategories.active, ConfigModules.active))
            ConfigModules.active = null
            ConfigModules.fn()
        }
    }

    fun forward() {
        if (forward.isEmpty()) return
        back.push(State(ConfigCategories.active, ConfigModules.active))

        val last = forward.removeLast()
        ConfigCategories.active = last.category
        ConfigModules.active = last.module
        ConfigCategories.fn()
        ConfigModules.fn()
    }

    private fun MutableList<State>.push(state: State) {
        add(state)
        while (size > 5) {
            removeFirst()
        }
    }
}
