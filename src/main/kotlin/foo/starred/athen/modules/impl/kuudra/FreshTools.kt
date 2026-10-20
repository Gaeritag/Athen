@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.abstraction.title.MinecraftTitle
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object FreshTools : Module(
    "Fresh tools",
    "Fresh notifier and timer for kuudra.",
    ConfigCategory.KUUDRA
) {
    private val alert by config.switch("Show alert", true)
    private val `alert$message` by config.switch("Alert message", true)
    private val `alert$message$t` by config.input("Message", "<red>Fresh tools!")
    private val `alert$title` by config.switch("Alert title", true)
    private val `alert$title$t` by config.input("Title", "<red>Fresh tools!")

    private val notify0 by config.group("Notifications")
    private val notify by notify0.switch("Notify party", true)
    private val `notify$message` by notify0.input("Notify message", "FRESH [#buildPerc]")
    private val `notify$unused` by notify0.variables("#buildPerc")

    private val timer by config.hud("Fresh timer") {
        val example = "Fresh: §c6.7s".formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (time == -1L) return@render

            val r = 10_000 - (System.currentTimeMillis() - time)
            if (r <= 0) return@render fn()

            MinecraftFontRenderer.extract(graphics, "Fresh: §c${(r / 1000).parseDuration(decimals = 1)}", 0, 0)
        }
    }

    private var time: Long = -1

    init {
        on<LocationEvent.Server.Connect> {
            fn()
        }

        on<MessageEvent.Chat.Receive> {
            if (!timer.state.value && !alert) return@on
            if (KuudraAPI.phase != KuudraPhase.Build) return@on
            if (stripped != "Your Fresh Tools Perk bonus doubles your building speed for the next 10 seconds!") return@on

            time = System.currentTimeMillis()

            if (!alert && !notify) return@on
            if (alert && `alert$message`) `alert$message$t`.mod()
            if (alert && `alert$title`) MinecraftTitle.display(`alert$title$t`.parse())
            if (notify) fn0()
        }
    }

    private fun fn() {
        time = -1
    }

    private fun fn0() {
        val vector = client.player?.blockPosition() ?: return
        val int = KuudraSupply.every.minByOrNull { it.buildPos.distSqr(vector) }?.progress ?: return
        val a = `notify$message`.replace("#buildPerc", "$int%")

        MinecraftCommands.client("pc $a")
    }
}
