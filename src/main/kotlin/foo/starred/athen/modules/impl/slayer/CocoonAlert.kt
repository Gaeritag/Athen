@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.abstraction.title.MinecraftTitle
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object CocoonAlert : Module(
    "Cocoon alert",
    "Alerts you when you cocoon your slayer boss!",
    ConfigCategory.SLAYER
) {
    private val alert by config.switch("Show alert", true)
    private val `alert$message` by config.input("Alert message", "<red>Boss cocooned!")
    private val `alert$sound` by config.sound("Alert sound")

    private val timer by config.hud("Cocoon timer") {
        val example = "Cocoon: §c4.6s".formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (time == 0L) return@render

            val t = time - System.currentTimeMillis()
            if (t <= 0L) {
                time = 0
                return@render
            }

            MinecraftFontRenderer.extract(graphics, "Cocoon: §c${t.div(1000).parseDuration(decimals = 1)}", 0, 0)
        }
    }

    private var time: Long = 0

    init {
        on<MessageEvent.Chat.Receive> {
            if (stripped.trim() != "YOU COCOONED YOUR SLAYER BOSS") return@on

            `alert$sound`.play()
            if (alert) MinecraftTitle.display(`alert$message`.parse())
            if (timer.state.value) time = System.currentTimeMillis() + 6000
        }
    }
}
