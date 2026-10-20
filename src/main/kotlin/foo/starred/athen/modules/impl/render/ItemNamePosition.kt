@file:Suppress("ConstPropertyName")

package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.hud.impl.HudRenderer
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.format.extension.component.formattedText

@LoadableAthenModule
object ItemNamePosition : Module(
    "Item name position",
    "Changes the positions of item display names",
    ConfigCategory.RENDER
) {
    private val example = "§cEpic item".formattedText()
    private val int by lazy {
        MinecraftFontMeasurer.width(example)
    }

    val hud by config.hud("Item name") {
        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }
    }

    @JvmStatic
    fun x(): Int {
        return ((hud.coordinate.x + int / 2) * HudRenderer.scale).toInt()
    }

    @JvmStatic
    fun y(): Int {
        return (hud.coordinate.y * HudRenderer.scale).toInt()
    }
}
