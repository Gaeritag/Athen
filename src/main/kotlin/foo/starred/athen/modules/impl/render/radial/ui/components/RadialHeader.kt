@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.render.radial.ui.components

import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.modules.impl.render.radial.RadialMenu
import foo.starred.athen.modules.impl.render.radial.ui.components.RadialEditableText.Companion.radialEditableText
import foo.starred.athen.modules.impl.render.radial.ui.editor.RadialEditor
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FillSizeConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.utils.literal

class RadialHeader(side: IPrimitiveElement<*>) {
    var title: RadialEditableText
        private set

    init {
        val head = roundedRectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 20))
            position = FixedPositionConstraint(0, 0)
            color = Mocha.Base.argb
            radius = CascadeGeometricRadius.ZERO

            effect(OutlineEffect {
                color = Mocha.Surface0.argb
            })

            attach(side)
        }

        roundedRectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 4)
            color = Mocha.Surface1.argb
            radius = CascadeGeometricRadius(2.5f)

            effect(OutlineEffect {
                color = Mocha.Overlay0.argb
            })

            on<MouseEvent.Press> {
                this.cancel()
                if (button != 0) return@on

                RadialEditor.save()
                val list0 = RadialEditor.names
                RadialEditor.switch(list0[(list0.indexOf(RadialMenu.active) - 1 + list0.size) % list0.size])
            }

            attach(head)
            adopt(image {
                location = ResourceAPI.identify("textures/gui/chevron.png")
                rotation = -90f
                color = Mocha.Text.argb
                position = CenterPositionConstraint()
                size = FixedSizeConstraint(6f, 6f)
                interact = false
            })
        }

        val box0 = container {
            size = FixedSizeConstraint(70, 20)
            position = FixedPositionConstraint(16, 0)

            attach(head)
        }

        title = radialEditableText {
            size = FillSizeConstraint()
            position = FixedPositionConstraint(0, 0)
            textSize = 8.5f
            color0 = Mocha.Lavender.argb
            value = RadialMenu.active

            commit {
                RadialEditor.rename()
            }

            attach(box0)
        }

        val box1 = container {
            size = FixedSizeConstraint(40, 14)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -4)

            attach(head)
        }

        roundedRectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            radius = CascadeGeometricRadius(2.5f)

            effect(OutlineEffect {
                color = Mocha.Overlay0.argb
            })

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on

                RadialEditor.save()
                val list0 = RadialEditor.names
                RadialEditor.switch(list0[(list0.indexOf(RadialMenu.active) + 1) % list0.size])
            }

            attach(box1)
            adopt(image {
                location = ResourceAPI.identify("textures/gui/chevron.png")
                rotation = 90f
                color = Mocha.Text.argb
                position = CenterPositionConstraint()
                size = FixedSizeConstraint(6f, 6f)
                interact = false
            })
        }

        roundedRectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            radius = CascadeGeometricRadius(2.5f)

            effect(OutlineEffect {
                color = Mocha.Overlay0.argb
            })

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on
                RadialEditor.save()

                var s0 = "New"
                var i0 = 1
                val list0 = RadialEditor.names
                while (list0.contains(s0)) {
                    s0 = "New ${++i0}"
                }

                RadialMenu.add(s0)
                RadialEditor.working.clear()
                RadialEditor.working.addAll(RadialMenu.slots)
                RadialEditor.reload(0, -1)
            }

            attach(box1)
            adopt(text {
                wrapper = CascadeTextWrapper
                text = "+".literal()
                textSize = 9f
                color = Mocha.Green.argb
                position = CenterPositionConstraint()
            })
        }

        roundedRectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            radius = CascadeGeometricRadius(2.5f)

            effect(OutlineEffect {
                color = Mocha.Overlay0.argb
            })

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on
                if (RadialEditor.names.size <= 1) return@on

                RadialEditor.commit()
                RadialMenu.slots.clear()
                RadialMenu.slots.addAll(RadialEditor.working)
                RadialMenu.delete(RadialMenu.active)
                RadialEditor.working.clear()
                RadialEditor.working.addAll(RadialMenu.slots)
                RadialEditor.reload(0, -1)
            }

            attach(box1)
            adopt(text {
                wrapper = CascadeTextWrapper
                text = "×".literal()
                textSize = 8.5f
                color = Mocha.Red.argb
                position = CenterPositionConstraint()
            })
        }
    }

    fun fn() {
        if (title.editing) return
        title.value = RadialMenu.active
    }
}
