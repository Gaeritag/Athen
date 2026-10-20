package foo.starred.athen.config.ui.pages.module.elements.input

import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.constraints.base.IPositionConstraint
import foo.starred.cascade.constraints.base.ISizeConstraint
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.CenterPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.effects.impl.OutlineEffect
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.time.Duration.Companion.milliseconds

class ConfigInputPreviewElement(private val value: () -> String) : RoundedRectanglePrimitive() {
    private val text = text {
        wrapper = CascadeTextWrapper
        textSize = 10f
        color = CascadeGeometricColor(MochaColorScheme.Text.argb)
        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.START)
        offset = CascadeGeometricOffset(4f, 4f)
    }

    private val tooltip = object : RoundedRectanglePrimitive() {
        override fun draw(graphics: GuiGraphicsExtractor) {
            graphics.nextStratum()
            super.draw(graphics)
        }
    }.apply {
        color = CascadeGeometricColor(MochaColorScheme.Base.argb)
        radius = CascadeGeometricRadius(4f)
        visible = false
        interact = false

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        size = object : ISizeConstraint {
            override fun _width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.width + 8f
            override fun _height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = text.height + 8f
        }

        adopt(text)
    }

    init {
        size = FixedSizeConstraint(14f, 14f)
        radius = CascadeGeometricRadius(4f)
        color = CascadeGeometricColor.TRANSPARENT

        effect(OutlineEffect {
            color = CascadeGeometricColor(MochaColorScheme.Surface1.argb)
            inset = false
        })

        adopt(text {
            wrapper = CascadeTextWrapper
            text = "?".literal()
            textSize = 10f
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
            position = CenterPositionConstraint()
        })

        on<MouseEvent.Move.Any> {
            if (!hovered) return@on

            tooltip.position = object : IPositionConstraint {
                override fun _x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = x.toFloat() + 5f
                override fun _y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Float = y.toFloat() + 5f
            }
        }

        on<MouseEvent.Move.Enter> {
            tooltip.visible = true
            text.text = value().parse()
            animate(150.milliseconds) {
                effect<OutlineEffect> {
                    ::color to CascadeGeometricColor(MochaColorScheme.Lavender.argb)
                }
            }
        }

        on<MouseEvent.Move.Exit> {
            tooltip.visible = false
            animate(150.milliseconds) {
                effect<OutlineEffect> {
                    ::color to CascadeGeometricColor(MochaColorScheme.Surface1.argb)
                }
            }
        }

        tooltip.attach(ConfigUI.scene)
    }

    companion object {
        fun configPreviewButtonElement(value: () -> String, block: ConfigInputPreviewElement.() -> Unit): ConfigInputPreviewElement {
            return ConfigInputPreviewElement(value).apply(block)
        }
    }
}
