package foo.starred.athen.config.ui.pages.main

import foo.starred.athen.config.Category
import foo.starred.athen.config.ui.ConfigUI.left
import foo.starred.athen.config.ui.pages.module.ConfigModules
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.utils.literal

object ConfigCategories {
    var active: Category = Category.INFO
        private set

    fun fn() {
        left.children.clear()

        var last: IPrimitiveElement<*>? = null
        for (a in Category.entries) {
            val bool = active == a
            val last0 = last

            if (a == Category.GENERAL) {
                last = container {
                    position = AnchorPositionConstraint({ last0!! }, PositionAnchor.BELOW, 0f, 6f)
                    size = FixedSizeConstraint(124f, 10f)
                    attach(left)

                    adopt(text {
                        type = CascadeTextPrimitiveRenderer
                        text = "Categories".literal()
                        textSize = 9f
                        color = Catppuccin.Mocha.Surface0.argb
                        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 2f, 0f)
                    })

                    adopt(rectangle {
                        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -2f, 0f)
                        size = FixedSizeConstraint(74f, 1f)
                        color = Catppuccin.Mocha.Surface0.argb
                    })
                }
            }

            val last1 = last

            last = roundedRectangle {
                position = if (last1 == null) FixedPositionConstraint(8f, 8f) else if (a == Category.GENERAL) AnchorPositionConstraint({ last1 }, PositionAnchor.BELOW, 0f, 6f) else AnchorPositionConstraint({ last1 }, PositionAnchor.BELOW, 0f, 4f)
                size = FixedSizeConstraint(124f, 22f)
                color = if (bool) Catppuccin.Mocha.Surface0.argb else Catppuccin.Mocha.Mantle.argb
                radius = RoundedRectangleRadius.of(4f)

                on<MouseEvent.Press> {
                    cancel()

                    if (button != 0) {
                        return@on
                    }

                    if (active == a) {
                        ConfigModules.active = null
                        ConfigModules.fn()
                        return@on
                    }

                    active = a
                    ConfigModules.active = null
                    fn()
                    ConfigModules.fn()
                }

                on<MouseEvent.Move.Enter> {
                    if (active == a) return@on
                    animateColor(Catppuccin.Mocha.Base.argb, 0.15f)
                }

                on<MouseEvent.Move.Exit> {
                    if (active == a) return@on
                    animateColor(Catppuccin.Mocha.Mantle.argb, 0.15f)
                }

                attach(left)
                adopt(text {
                    type = CascadeTextPrimitiveRenderer
                    text = a.displayName.literal()
                    textSize = 12f
                    color = if (bool) Catppuccin.Mocha.Lavender.argb else Catppuccin.Mocha.Subtext0.argb
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 8f, 0f)
                })
            }
        }
    }
}