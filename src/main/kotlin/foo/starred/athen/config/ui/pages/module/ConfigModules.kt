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
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.font.CascadeFonts
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.data.text.impl.CascadeTextPrimitiveRenderer
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.cascade.primitives.impl.RoundedRectanglePrimitive.Companion.roundedRectangle
import foo.starred.cascade.primitives.impl.TextPrimitive.Companion.text
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.brighten

object ConfigModules {
    var active: ConfigFeatureData? = null

    fun fn() {
        ConfigUI.right0.children.removeIf { it != ConfigUI.right }
        ConfigUI.right.children.clear()
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
                color = if (ConfigManager.get(v.configKey) as? Boolean ?: (v.default as? Boolean ?: false)) Catppuccin.Mocha.Lavender.argb.brighten(0.5f) else Catppuccin.Mocha.Base.argb
                radius = RoundedRectangleRadius.of(4f)
                border = true
                borderColor = Catppuccin.Mocha.Surface0.argb
                borderInset = false

                fun colors(bool: Boolean) {
                    val enabled = ConfigManager.get(v.configKey) as? Boolean ?: (v.default as? Boolean ?: false)

                    animateColor(when {
                        enabled && bool -> Catppuccin.Mocha.Lavender.argb.brighten(0.65f)
                        enabled -> Catppuccin.Mocha.Lavender.argb.brighten(0.55f)
                        bool -> Catppuccin.Mocha.Surface0.argb
                        else -> Catppuccin.Mocha.Base.argb
                    }, 0.15f)
                }

                on<MouseEvent.Move.Enter> {
                    colors(true)
                }

                on<MouseEvent.Move.Exit> {
                    colors(false)
                }

                attach(ConfigUI.right)
                adopt(text {
                    val name = if (CascadeFonts.loaded) CascadeFonts.arial.truncate(v.name, 12f, 115f) else v.name

                    type = CascadeTextPrimitiveRenderer
                    text = name.parse()
                    textSize = 12f
                    color = Catppuccin.Mocha.Text.argb
                    position = AlignPositionConstraint(PositionAlignment.START, PositionAlignment.CENTER, 10f, 0f)
                })

                adopt(rectangle {
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -26f, 0f)
                    size = FixedSizeConstraint(1f, 28f)
                    color = Catppuccin.Mocha.Surface1.argb
                    interact = false
                })

                val image = image {
                    location = ResourceAPI.identify("textures/gui/gear.png")
                    color = if (options) Catppuccin.Mocha.Text.argb else Catppuccin.Mocha.Surface1.argb
                    position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER, -6f, 0f)
                    size = FixedSizeConstraint(14f, 14f)
                    interact = false

                    attach(this@roundedRectangle)
                }

                on<MouseEvent.Press> {
                    cancel()

                    if (image.contains(x, y)) {
                        if (options) {
                            active = v
                            fn()
                        }

                        return@on
                    }

                    val bool = ConfigManager.get(v.configKey) as? Boolean ?: (v.default as? Boolean ?: false)
                    ConfigManager.update(v.configKey, !bool)
                    colors(hovered)
                }
            }

            if (i % 3 == 0) first = rect
            last = rect
        }
    }

    private fun ConfigFeatureData.matches(query: String): Boolean {
        if (query.isEmpty()) return true
        return name.contains(query, true) || description.contains(query, true) || options.any {
            it.name.contains(query, true) || it.description?.contains(query, true) == true
        }
    }
}