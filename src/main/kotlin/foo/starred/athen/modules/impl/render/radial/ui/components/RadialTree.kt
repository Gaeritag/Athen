package foo.starred.athen.modules.impl.render.radial.ui.components

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.modules.impl.render.radial.data.RadialSlot
import foo.starred.athen.modules.impl.render.radial.ui.editor.RadialEditor
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.MixedSizeConstraint
import foo.starred.cascade.constraints.impl.size.PercentSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.ItemPrimitive.Companion.item
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.ScrollablePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.component.literal

class RadialTree(private val side0: ScrollablePrimitive) {
    fun fn() {
        side0.children.clear()
        val main = RadialEditor.main
        val sub = RadialEditor.sub
        val m0 = RadialEditor.max
        val list0 = RadialEditor.working
        val list1 = RadialEditor.collapsed

        var i0 = 4
        for (i1 in list0.indices) {
            val b0 = i1 == main && sub < 0
            val b1 = list0[i1].sub.isNotEmpty()
            val b2 = b1 && i1 !in list1

            roundedRectangle {
                size = MixedSizeConstraint(PercentSizeConstraint(95f, 0f), FixedSizeConstraint(0, 22))
                position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
                offset = CascadeGeometricOffset(0f, i0)
                color = CascadeGeometricColor(if (b0) MochaColorScheme.Surface1.argb else MochaColorScheme.Mantle.argb)
                radius = CascadeGeometricRadius(4f)

                effect(OutlineEffect {
                    color = CascadeGeometricColor(if (b0) MochaColorScheme.Lavender.argb else MochaColorScheme.Surface0.argb)
                })

                on<MouseEvent.Press> {
                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                    cancel()
                    RadialEditor.commit()
                    RadialEditor.reload(i1, -1)
                }

                on<MouseEvent.Move.Enter> {
                    if (RadialEditor.main == i1 && RadialEditor.sub < 0) return@on
                    color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                }

                on<MouseEvent.Move.Exit> {
                    color = CascadeGeometricColor(if (RadialEditor.main == i1 && RadialEditor.sub < 0) MochaColorScheme.Surface1.argb else MochaColorScheme.Mantle.argb)
                }

                attach(side0)
                adopt(item {
                    item = list0[i1].item
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                    offset = CascadeGeometricOffset(4f, 0f)
                })

                adopt(text {
                    wrapper = CascadeTextWrapper
                    text = CascadeFonts.sans.truncate(list0[i1].name.ifBlank { "..." }, 9.5f, 54f).literal()
                    textSize = 9.5f
                    color = CascadeGeometricColor(if (b0) MochaColorScheme.Text.argb else MochaColorScheme.Subtext0.argb)
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                    offset = CascadeGeometricOffset(24f, 0f)
                })

                if (!b0) return@roundedRectangle
                adopt(container {
                    size = FixedSizeConstraint(40, 14)
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
                    offset = CascadeGeometricOffset(-4f, 0f)

                    adopt(roundedRectangle {
                        size = FixedSizeConstraint(12, 12)
                        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                        color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                        radius = CascadeGeometricRadius(2.5f)

                        effect(OutlineEffect {
                            color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                        })

                        on<MouseEvent.Press> {
                            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
                            if (i1 <= 0) return@on

                            RadialEditor.commit()
                            val tmp0 = list0[i1]
                            list0[i1] = list0[i1 - 1]
                            list0[i1 - 1] = tmp0
                            RadialEditor.reload(i1 - 1, -1)
                            cancel()
                        }

                        adopt(image {
                            location = ResourceAPI.identify("textures/gui/chevron.png")
                            rotation = 0f
                            size = FixedSizeConstraint(6f, 6f)
                            color = CascadeGeometricColor(MochaColorScheme.Text.argb)
                            position = CenterPositionConstraint()
                            interact = false
                        })
                    })

                    adopt(roundedRectangle {
                        size = FixedSizeConstraint(12, 12)
                        position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.CENTER)
                        color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                        radius = CascadeGeometricRadius(2.5f)

                        effect(OutlineEffect {
                            color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                        })

                        on<MouseEvent.Press> {
                            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
                            if (i1 >= list0.lastIndex) return@on

                            RadialEditor.commit()
                            val tmp0 = list0[i1]
                            list0[i1] = list0[i1 + 1]
                            list0[i1 + 1] = tmp0
                            RadialEditor.reload(i1 + 1, -1)
                            cancel()
                        }

                        adopt(image {
                            location = ResourceAPI.identify("textures/gui/chevron.png")
                            rotation = 180f
                            size = FixedSizeConstraint(6f, 6f)
                            color = CascadeGeometricColor(MochaColorScheme.Text.argb)
                            position = CenterPositionConstraint()
                            interact = false
                        })
                    })

                    adopt(roundedRectangle {
                        size = FixedSizeConstraint(12, 12)
                        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
                        color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                        radius = CascadeGeometricRadius(2.5f)

                        effect(OutlineEffect {
                            color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                        })

                        on<MouseEvent.Press> {
                            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                            RadialEditor.commit()
                            list0.removeAt(i1)
                            RadialEditor.reload(maxOf(0, i1 - 1), -1)
                            cancel()
                        }

                        adopt(text {
                            wrapper = CascadeTextWrapper
                            text = "×".literal()
                            textSize = 8.5f
                            color = CascadeGeometricColor(MochaColorScheme.Red.argb)
                            position = CenterPositionConstraint()
                        })
                    })
                })
            }

            i0 += 24

            if (b2) {
                for (i2 in list0[i1].sub.indices) {
                    val b3 = i1 == main && i2 == sub

                    roundedRectangle {
                        size = MixedSizeConstraint(PercentSizeConstraint(84f, 0f), FixedSizeConstraint(0, 20))
                        position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.START)
                        offset = CascadeGeometricOffset(-4f, i0)
                        color = CascadeGeometricColor(if (b3) MochaColorScheme.Surface1.argb else MochaColorScheme.Mantle.argb)
                        radius = CascadeGeometricRadius(4f)

                        effect(OutlineEffect {
                            color = CascadeGeometricColor(if (b3) MochaColorScheme.Lavender.argb else MochaColorScheme.Surface0.argb)
                        })

                        on<MouseEvent.Press> {
                            if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                            RadialEditor.commit()
                            RadialEditor.reload(i1, i2)
                            cancel()
                        }

                        on<MouseEvent.Move.Enter> {
                            if (RadialEditor.main == i1 && RadialEditor.sub == i2) return@on
                            color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                        }

                        on<MouseEvent.Move.Exit> {
                            color = CascadeGeometricColor(if (RadialEditor.main == i1 && RadialEditor.sub == i2) MochaColorScheme.Surface1.argb else MochaColorScheme.Mantle.argb)
                        }

                        attach(side0)

                        adopt(item {
                            item = list0[i1].sub[i2].item
                            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                            offset = CascadeGeometricOffset(4f, 0f)
                        })

                        adopt(text {
                            wrapper = CascadeTextWrapper
                            text = CascadeFonts.sans.truncate(list0[i1].sub[i2].name.ifBlank { "..." }, 9f, 42f).literal()
                            textSize = 9f
                            color = CascadeGeometricColor(if (b3) MochaColorScheme.Text.argb else MochaColorScheme.Subtext0.argb)
                            position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                            offset = CascadeGeometricOffset(22f, 0f)
                        })

                        if (!b3) return@roundedRectangle
                        adopt(container {
                            size = FixedSizeConstraint(40, 14)
                            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
                            offset = CascadeGeometricOffset(-4f, 0f)

                            adopt(roundedRectangle {
                                size = FixedSizeConstraint(12, 12)
                                position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                                radius = CascadeGeometricRadius(2.5f)

                                effect(OutlineEffect {
                                    color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                                })

                                on<MouseEvent.Press> {
                                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
                                    if (i2 <= 0) return@on

                                    RadialEditor.commit()
                                    val parent0 = list0[i1]
                                    val list2 = parent0.sub.toMutableList()
                                    val tmp1 = list2[i2]
                                    list2[i2] = list2[i2 - 1]
                                    list2[i2 - 1] = tmp1
                                    parent0.sub = list2
                                    RadialEditor.reload(i1, i2 - 1)
                                    cancel()
                                }

                                adopt(image {
                                    location = ResourceAPI.identify("textures/gui/chevron.png")
                                    rotation = 0f
                                    size = FixedSizeConstraint(6f, 6f)
                                    color = CascadeGeometricColor(MochaColorScheme.Text.argb)
                                    position = CenterPositionConstraint()
                                    interact = false
                                })
                            })

                            adopt(roundedRectangle {
                                size = FixedSizeConstraint(12, 12)
                                position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.CENTER)
                                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                                radius = CascadeGeometricRadius(2.5f)

                                effect(OutlineEffect {
                                    color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                                })

                                on<MouseEvent.Press> {
                                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on
                                    if (i2 >= list0[i1].sub.lastIndex) return@on

                                    RadialEditor.commit()
                                    val parent0 = list0[i1]
                                    val list2 = parent0.sub.toMutableList()
                                    val tmp1 = list2[i2]
                                    list2[i2] = list2[i2 + 1]
                                    list2[i2 + 1] = tmp1
                                    parent0.sub = list2
                                    RadialEditor.reload(i1, i2 + 1)
                                    cancel()
                                }

                                adopt(image {
                                    location = ResourceAPI.identify("textures/gui/chevron.png")
                                    rotation = 180f
                                    size = FixedSizeConstraint(6f, 6f)
                                    color = CascadeGeometricColor(MochaColorScheme.Text.argb)
                                    position = CenterPositionConstraint()
                                    interact = false
                                })
                            })

                            adopt(roundedRectangle {
                                size = FixedSizeConstraint(12, 12)
                                position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
                                color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                                radius = CascadeGeometricRadius(2.5f)

                                effect(OutlineEffect {
                                    color = CascadeGeometricColor(MochaColorScheme.Overlay0.argb)
                                })

                                on<MouseEvent.Press> {
                                    if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                                    RadialEditor.commit()
                                    val parent0 = list0[i1]
                                    val list2 = parent0.sub.toMutableList()
                                    list2.removeAt(i2)
                                    parent0.sub = list2
                                    RadialEditor.reload(i1, if (parent0.sub.isEmpty()) -1 else maxOf(0, i2 - 1))
                                    cancel()
                                }

                                adopt(text {
                                    wrapper = CascadeTextWrapper
                                    text = "×".literal()
                                    textSize = 8.5f
                                    color = CascadeGeometricColor(MochaColorScheme.Red.argb)
                                    position = CenterPositionConstraint()
                                })
                            })
                        })
                    }

                    rectangle {
                        size = FixedSizeConstraint(1, 14)
                        position = FixedPositionConstraint(10, i0 + 3)
                        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                        interact = false

                        attach(side0)
                    }

                    i0 += 22
                }
            }

            if (b2 || (b0 && !b1)) {
                roundedRectangle {
                    size = MixedSizeConstraint(PercentSizeConstraint(84f, 0f), FixedSizeConstraint(0, 18))
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.START)
                    offset = CascadeGeometricOffset(-4f, i0)
                    color = CascadeGeometricColor(MochaColorScheme.Mantle.argb)
                    radius = CascadeGeometricRadius(4f)

                    effect(OutlineEffect {
                        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                    })

                    attach(side0)

                    on<MouseEvent.Press> {
                        if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                        cancel()
                        RadialEditor.commit()
                        val parent0 = list0.getOrNull(i1) ?: return@on
                        if (parent0.sub.size >= m0) return@on

                        parent0.sub += RadialSlot("New Sub")
                        list1.remove(i1)
                        RadialEditor.reload(i1, list0[i1].sub.lastIndex)
                    }

                    on<MouseEvent.Move.Enter> {
                        color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                    }

                    on<MouseEvent.Move.Exit> {
                        color = CascadeGeometricColor(MochaColorScheme.Mantle.argb)
                    }

                    adopt(text {
                        wrapper = CascadeTextWrapper
                        text = "+ Sub".literal()
                        textSize = 8.5f
                        color = CascadeGeometricColor(MochaColorScheme.Green.argb)
                        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER)
                        offset = CascadeGeometricOffset(6f, 0f)
                    })
                }

                rectangle {
                    size = FixedSizeConstraint(1, 14)
                    position = FixedPositionConstraint(10, i0 + 2)
                    color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
                    interact = false

                    attach(side0)
                }

                i0 += 22
            }
        }

        roundedRectangle {
            size = MixedSizeConstraint(PercentSizeConstraint(95f, 0f), FixedSizeConstraint(0, 22))
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
            offset = CascadeGeometricOffset(0f, i0)
            color = CascadeGeometricColor(MochaColorScheme.Mantle.argb)
            radius = CascadeGeometricRadius(4f)

            effect(OutlineEffect {
                color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            })

            attach(side0)

            on<MouseEvent.Press> {
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                RadialEditor.commit()
                list0.add(RadialSlot("New Slot"))
                RadialEditor.reload(list0.lastIndex, -1)
                cancel()
            }

            on<MouseEvent.Move.Enter> {
                color = CascadeGeometricColor(MochaColorScheme.Surface0.argb)
            }

            on<MouseEvent.Move.Exit> {
                color = CascadeGeometricColor(MochaColorScheme.Mantle.argb)
            }

            adopt(text {
                wrapper = CascadeTextWrapper
                text = "+ Slot".literal()
                textSize = 9.5f
                color = CascadeGeometricColor(MochaColorScheme.Green.argb)
                position = CenterPositionConstraint()
            })
        }

        container {
            size = FixedSizeConstraint(10, 4)
            position = AlignPositionConstraint(PositionAlignment.CENTER, PositionAlignment.START)
            offset = CascadeGeometricOffset(0f, i0 + 24)
            interact = false
            attach(side0)
        }
    }
}
