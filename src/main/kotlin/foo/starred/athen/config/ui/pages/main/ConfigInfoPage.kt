package foo.starred.athen.config.ui.pages.main

import foo.starred.athen.config.ui.ConfigUI.right
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.cascade.animation.data.AnimatableColor.Companion.animateColor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.data.PositionAnchor
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.AnchorPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.literal
import foo.starred.snowbird.utils.open

object ConfigInfoPage {
    fun fn() {
        val links = container {
            position = FixedPositionConstraint(14f, 14f)
            size = FixedSizeConstraint(482f, 28f)

            attach(right)

            val list = listOf("Discord" to "https://discord.gg/starred", "Source" to "https://github.com/skies-starred/Athen", "Patreon" to "https://patreon.com/starredskies")
            var last: IPrimitiveElement<*>? = null

            for ((k, v) in list.reversed()) {
                val last0 = last
                last = roundedRectangle {
                    position = if (last0 == null) AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, 0f, 0f) else AnchorPositionConstraint({ last0 }, PositionAnchor.LEFT, -10f, 0f)
                    size = FixedSizeConstraint(154f, 28f)
                    color = Catppuccin.Mocha.Base.argb
                    radius = CascadeGeometricRadius(4f)

                    effect(OutlineEffect {
                        color = Catppuccin.Mocha.Surface0.argb
                        inset = false
                    })

                    on<MouseEvent.Move.Enter> {
                        animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
                    }

                    on<MouseEvent.Move.Exit> {
                        animateColor(Catppuccin.Mocha.Base.argb, 0.15f)
                    }

                    on<MouseEvent.Press> {
                        cancel()
                        v.open()
                    }

                    attach(this@container)
                    adopt(text {
                        wrapper = CascadeTextWrapper
                        text = k.literal()
                        textSize = 12f
                        color = Catppuccin.Mocha.Text.argb
                        position = CenterPositionConstraint()
                    })
                }
            }
        }

        val card0 = roundedRectangle {
            position = AnchorPositionConstraint({ links }, PositionAnchor.BELOW, 0f, 10f)
            size = FixedSizeConstraint(482f, 96f)
            color = Catppuccin.Mocha.Base.argb
            radius = CascadeGeometricRadius(4f)

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface0.argb
                inset = false
            })

            attach(right)
            adopt(text {
                wrapper = CascadeTextWrapper
                text = "<bold><#FDCCDA>A<#FCDDD3>t<#FAEDCB>h<#F0E2D7>e<#E5D8E4>n<#DBCDF0> <white>Configuration".parse()
                textSize = 18f
                color = Catppuccin.Mocha.Text.argb
                position = FixedPositionConstraint(14f, 14f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Run /athen help to view all commands".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 40f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Run /athen hud to open hud editor".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 56f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Donate to get cosmetics such as custom name and size along with other perks!".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 72f)
            })
        }

        roundedRectangle {
            position = AnchorPositionConstraint({ card0 }, PositionAnchor.BELOW, 0f, 10f)
            size = FixedSizeConstraint(482f, 96f)
            color = Catppuccin.Mocha.Base.argb
            radius = CascadeGeometricRadius(4f)

            effect(OutlineEffect {
                color = Catppuccin.Mocha.Surface0.argb
                inset = false
            })

            attach(right)
            adopt(text {
                wrapper = CascadeTextWrapper
                text = "<bold>Changelogs".parse()
                textSize = 18f
                color = Catppuccin.Mocha.Text.argb
                position = FixedPositionConstraint(14f, 14f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Changelog 1".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 40f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Changelog 2".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 56f)
            })

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "- Changelog 3".literal()
                textSize = 12f
                color = Catppuccin.Mocha.Subtext0.argb
                position = FixedPositionConstraint(14f, 72f)
            })
        }
    }
}
