package foo.starred.athen.config.ui.pages.module

import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.Category
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.feature.ConfigFeatureData
import foo.starred.athen.config.ui.ConfigUI
import foo.starred.athen.config.ui.pages.main.ConfigCategories
import foo.starred.athen.config.ui.pages.main.ConfigInfoPage
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
import foo.starred.cascade.graphics.font.CascadeFonts
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.cascade.wrappers.text.impl.CascadeTextWrapper
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.brighten
import foo.starred.snowbird.utils.withAlpha
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.math.abs

object ConfigModules {
    var active: ConfigFeatureData? = null

    fun fn() {
        for (child in ConfigUI.right0.children.filter { it != ConfigUI.right }) child.forEach { it.detach() }
        for (child in ConfigUI.right.children) child.forEach { it.detach() }

        ConfigUI.headerText.text = "<bold><#FDCCDA>A<#FCDDD3>t<#FAEDCB>h<#F0E2D7>e<#E5D8E4>n<#DBCDF0>".parse()
        if (active != null) return ConfigModuleSettingsPage.fn(active!!)
        if (ConfigCategories.active == Category.INFO) return ConfigInfoPage.fn()

        val query = ConfigUI.searchBar.value.trim()
        val features = (ConfigManager.features[ConfigCategories.active] ?: return).filter { it.matches(query) }.sortedWith(compareByDescending<ConfigFeatureData> { it.name.startsWith(query, true) }.thenBy { it.name })
        var first: IPrimitiveElement<*>? = null
        var last: IPrimitiveElement<*>? = null

        for ((i, v) in features.withIndex()) {
            val first0 = first
            val last0 = last
            val options = v.options.isNotEmpty()

            val rect = roundedRectangle {
                position =
                    if (i == 0) FixedPositionConstraint(14f, 14f)
                    else if (i % 3 == 0) AnchorPositionConstraint({ first0!! }, PositionAnchor.BELOW, 0f, 10f)
                    else AnchorPositionConstraint({ last0!! }, PositionAnchor.RIGHT, 10f, 0f)

                size = FixedSizeConstraint(154f, 28f)
                color = Catppuccin.Mocha.Base.argb
                radius = CascadeGeometricRadius(4f)

                effect(OutlineEffect {
                    color = Catppuccin.Mocha.Surface0.argb
                    inset = false
                })

                attach(ConfigUI.right)
                adopt(roundedRectangle {
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 0f, 0f)
                    size = FixedSizeConstraint(127f, 28f)
                    radius = CascadeGeometricRadius(4f, 0f, 4f, 0f)

                    fun colors(bool: Boolean) {
                        val enabled = ConfigManager.get(v.configKey) as? Boolean ?: (v.default as? Boolean ?: false)

                        animateColor(when {
                            enabled && bool -> Catppuccin.Mocha.Lavender.argb.brighten(0.65f)
                            enabled -> Catppuccin.Mocha.Lavender.argb.brighten(0.55f)
                            bool -> Catppuccin.Mocha.Surface0.argb
                            else -> Catppuccin.Mocha.Base.argb
                        }, 0.15f)
                    }

                    colors(false)

                    on<MouseEvent.Move.Enter> {
                        colors(true)
                    }

                    on<MouseEvent.Move.Exit> {
                        colors(false)
                    }

                    on<MouseEvent.Press> {
                        cancel()

                        val bool = ConfigManager.get(v.configKey) as? Boolean ?: (v.default as? Boolean ?: false)
                        ConfigManager.update(v.configKey, !bool)
                        colors(hovered)
                    }

                    adopt(text {
                        val name = if (CascadeFonts.loaded) CascadeFonts.arial.truncate(v.name, 12f, 115f) else v.name

                        wrapper = CascadeTextWrapper
                        text = name.parse()
                        textSize = 12f
                        color = Catppuccin.Mocha.Text.argb
                        position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 10f, 0f)
                    })
                })

                adopt(rectangle {
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -27f, 0f)
                    size = FixedSizeConstraint(1f, 28f)
                    color = Catppuccin.Mocha.Surface0.argb
                    interact = false
                })

                adopt(roundedRectangle {
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, 0f, 0f)
                    size = FixedSizeConstraint(27f, 28f)
                    radius = CascadeGeometricRadius(0f, 4f, 0f, 4f)
                    color = Catppuccin.Mocha.Base.argb

                    adopt(image {
                        location = ResourceAPI.identify("textures/gui/gear.png")
                        color = if (options) Catppuccin.Mocha.Text.argb else Catppuccin.Mocha.Surface1.argb
                        position = CenterPositionConstraint()
                        size = FixedSizeConstraint(14f, 14f)
                        interact = false
                    })

                    if (!options) return@roundedRectangle
                    on<MouseEvent.Move.Enter> {
                        animateColor(Catppuccin.Mocha.Surface0.argb, 0.15f)
                    }

                    on<MouseEvent.Move.Exit> {
                        animateColor(Catppuccin.Mocha.Base.argb, 0.15f)
                    }

                    on<MouseEvent.Press> {
                        active = v
                        fn()
                        cancel()
                    }
                })
            }

            if (i % 3 == 0) first = rect
            last = rect
        }

        if (first != null) {
            container {
                position = AnchorPositionConstraint({ first }, PositionAnchor.BELOW, 0f, 14f)
                size = FixedSizeConstraint(1f, 1f)
                interact = false
                attach(ConfigUI.right)
            }
        }

        val total = (features.size + 2) / 3
        val max = (28f * total + 10f * (total - 1).coerceAtLeast(0) + 28f) - 318f
        if (max <= 0f) return

        object : RectanglePrimitive() {
            override fun render(graphics: GuiGraphicsExtractor) {
                val parent = parent ?: return
                val scroll = abs(ConfigUI.right.scroll)
                val remaining = (max - scroll).coerceAtLeast(0f)
                val base = Catppuccin.Mocha.Crust.argb
                val x = parent.x.toInt()
                val y = parent.y.toInt()
                val w = parent.width.toInt()
                val h = parent.height.toInt()

                if (scroll > 1f) {
                    val alpha = (scroll / 24f).coerceIn(0f, 1f)
                    graphics.fillGradient(x, y, x + w, y + 23, base.withAlpha(alpha), base.withAlpha(alpha * 0.667f))
                    graphics.fillGradient(x, y + 23, x + w, y + 46, base.withAlpha(alpha * 0.667f), base.withAlpha(0f))
                }

                if (remaining > 1f) {
                    val alpha = (remaining / 24f).coerceIn(0f, 1f)
                    graphics.fillGradient(x, y + h - 46, x + w, y + h - 23, base.withAlpha(0f), base.withAlpha(alpha * 0.667f))
                    graphics.fillGradient(x, y + h - 23, x + w, y + h, base.withAlpha(alpha * 0.667f), base.withAlpha(alpha))
                }
            }
        }.apply {
            color = 0
            interact = false
            attach(ConfigUI.right0)
        }
    }

    private fun ConfigFeatureData.matches(query: String): Boolean {
        if (query.isEmpty()) return true
        return name.contains(query, true) || description.contains(query, true) || options.any { it.name.contains(query, true) || it.description?.contains(query, true) == true }
    }
}
