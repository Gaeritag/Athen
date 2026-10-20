@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.modules.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.format.extension.component.formattedText

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object LagDetector : Module(
    "Lag detector",
    "Displays a timer since the last server tick if it was older than the threshold.",
    ConfigCategory.GENERAL
) {
    private val threshold by config.slider("Threshold", 750, 100, 5000, "ms")
    private val notify = config.switch("Send message").unique("notify")
    private val party by config.switch("Notify party")
    private val text by config.input("Message", "Lag detected!")

    private val hud by config.hud("Lag display") {
        val example = "§c67ms".formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (last == 0L) return@render
            if (client.player == null) return@render

            val t = System.currentTimeMillis() - last
            if (t <= threshold) return@render

            MinecraftFontRenderer.extract(graphics, "§c${t}ms", 0, 0)
        }
    }

    private var last = 0L
    private var bool = false

    init {
        on<TickEvent.Client.End> {
            if (last == 0L) return@on
            if (client.player == null) return@on

            val t = System.currentTimeMillis() - last
            if (t <= threshold) return@on
            if (bool) return@on

            bool = true
            text.mod()

            if (party) {
                MinecraftCommands.client("/pc $text")
            }
        }.runWhen(notify.state)

        on<TickEvent.Server> {
            last = System.currentTimeMillis()
            bool = false
        }

        on<LocationEvent.Server.Connect> {
            last = 0
            bool = false
        }
    }
}
