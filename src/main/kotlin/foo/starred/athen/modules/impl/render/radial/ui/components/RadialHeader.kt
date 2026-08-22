@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.render.radial.ui.components

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.rendering.ui.components.impl.TextFieldComponent
import foo.starred.athen.api.rendering.ui.components.impl.TextFieldComponent.Companion.textField
import foo.starred.athen.modules.impl.render.radial.RadialMenu
import foo.starred.athen.modules.impl.render.radial.ui.editor.RadialEditor
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.events.impl.KeyEvent
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.RectanglePrimitive
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.TextPrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.utils.literal

class RadialHeader(side: RectanglePrimitive) {
    var text00: TextPrimitive
        private set

    var field0: TextFieldComponent
        private set

    init {
        val head = rectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 24))
            position = FixedPositionConstraint(0, 0)
            color = Mocha.Base.argb
            border = true
            borderColor = Mocha.Surface0.argb

            attach(side)
        }

        rectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 4)
            color = Mocha.Surface1.argb
            border = true
            borderColor = Mocha.Overlay0.argb

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on

                RadialEditor.save()
                val list0 = RadialEditor.names
                RadialEditor.switch(list0[(list0.indexOf(RadialMenu.active) - 1 + list0.size) % list0.size])
            }

            attach(head)
            adopt(text {
                text = "<".literal()
                color = Mocha.Text.argb
                position = CenterPositionConstraint()
            })
        }

        val box0 = container {
            size = FixedSizeConstraint(70, 24)
            position = FixedPositionConstraint(16, 0)

            attach(head)
        }

        text00 = text {
            text = RadialMenu.active.literal()
            color = Mocha.Lavender.argb
            position = CenterPositionConstraint()
            interact = true

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on
                if (RadialEditor.editing) return@on RadialEditor.rename()

                RadialEditor.unfocus()
                RadialEditor.editing = true
                field0.value = RadialMenu.active
                field0.cursor = field0.value.length
                field0.visible = true
                text00.visible = false
                RadialEditor.scene.focused = field0
            }

            attach(box0)
        }

        field0 = textField {
            size = MixedSizeConstraint(PercentSizeConstraint(90f, 0f), FixedSizeConstraint(0, 16))
            position = CenterPositionConstraint()
            placeholder = "Config..."
            visible = false

            on<KeyEvent.Press> {
                if (key == InputConstants.KEY_RETURN) {
                    RadialEditor.rename()
                    cancel()
                    return@on
                }

                if (key == InputConstants.KEY_ESCAPE) {
                    RadialEditor.editing = false
                    field0.visible = false
                    text00.visible = true
                    RadialEditor.scene.focused = null
                    cancel()
                }
            }

            attach(box0)
        }

        val box1 = container {
            size = FixedSizeConstraint(40, 14)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -4)

            attach(head)
        }

        rectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            border = true
            borderColor = Mocha.Overlay0.argb

            on<MouseEvent.Press> {
                cancel()
                if (button != 0) return@on

                RadialEditor.save()
                val list0 = RadialEditor.names
                RadialEditor.switch(list0[(list0.indexOf(RadialMenu.active) + 1) % list0.size])
            }

            attach(box1)
            adopt(text {
                text = ">".literal()
                color = Mocha.Text.argb
                position = CenterPositionConstraint()
            })
        }

        rectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            border = true
            borderColor = Mocha.Overlay0.argb

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
                text = "+".literal()
                color = Mocha.Green.argb
                position = CenterPositionConstraint()
            })
        }

        rectangle {
            size = FixedSizeConstraint(12, 12)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, 0)
            color = Mocha.Surface1.argb
            border = true
            borderColor = Mocha.Overlay0.argb

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
                text = "×".literal()
                color = Mocha.Red.argb
                position = CenterPositionConstraint()
            })
        }
    }

    fun fn() {
        text00.text = RadialMenu.active.literal()
        text00.visible = !RadialEditor.editing
        field0.visible = RadialEditor.editing
    }
}