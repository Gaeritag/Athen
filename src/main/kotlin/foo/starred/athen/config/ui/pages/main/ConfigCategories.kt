package foo.starred.athen.config.ui.pages.main

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.config.ui.ConfigUI.left
import foo.starred.athen.config.ui.pages.module.ConfigModules
import foo.starred.athen.config.ui.pages.module.ConfigModulesNavigationState
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.component.literal
import kotlin.time.Duration.Companion.milliseconds

object ConfigCategories {
    var active: ConfigCategory = ConfigCategory.INFO

    fun fn() {
        left.children.clear()

        var last: IPrimitiveElement<*>? = null
        for (a in ConfigCategory.entries) {
            val bool = active == a
            val last1 = last

            last = roundedRectangle {
                position = if (last1 == null) FixedPositionConstraint(8f, 8f) else AnchorPositionConstraint({ last1 }, PositionAnchor.BELOW)
                offset = if (last1 == null) CascadeGeometricOffset.ZERO else if (a == ConfigCategory.GENERAL) CascadeGeometricOffset(0f, 6f) else CascadeGeometricOffset(0f, 4f)
                size = FixedSizeConstraint(124f, 22f)
                color = if (bool) CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.30f)) else CascadeGeometricColor.TRANSPARENT
                radius = CascadeGeometricRadius(4f)

                if (bool) {
                    effect(OutlineEffect {
                        color = CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.50f))
                        inset = false
                    })
                }

                on<MouseEvent.Press> {
                    cancel()

                    if (button != InputConstants.MOUSE_BUTTON_LEFT) {
                        return@on
                    }

                    if (active == a && ConfigModules.active == null) {
                        return@on
                    }

                    ConfigModulesNavigationState.navigate(a, null)
                }

                on<MouseEvent.Move.Enter> {
                    if (active == a) return@on

                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor(MochaColorScheme.Lavender.alpha(0.2f))
                    }
                }

                on<MouseEvent.Move.Exit> {
                    if (active == a) return@on

                    animate(150.milliseconds) {
                        ::color to CascadeGeometricColor.TRANSPARENT
                    }
                }

                attach(left)
                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = a.displayName.literal()
                    textSize = 12f
                    color = CascadeGeometricColor(if (bool) MochaColorScheme.Lavender.argb else MochaColorScheme.Subtext0.argb)
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                    offset = CascadeGeometricOffset(8f, 0f)
                })
            }
        }
    }
}
