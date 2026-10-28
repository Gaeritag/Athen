package foo.starred.athen.modules.impl

import foo.starred.athen.Athen
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.utils.command
import foo.starred.kontext.abstraction.chat.generic.MinecraftChat
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
object Dev {
    private val keyRegex = Regex("(?<!^)([A-Z])")

    init {
        command {
            "toggle" / "dev" {
                Athen.Data.debug = !Athen.Data.debug
                val a = if (Athen.Data.debug) "<green>Enabled" else "<red>Disabled"

                "Debug mode is now: $a<r>.".mod()
            }

            "toggle" / "feature" / string("key") {
                val key = string("key")

                val b = ConfigManager.get(key) as? Boolean ?: return@string "Not a valid feature!".mod()
                ConfigManager.update(key, !b)

                val s = key.replace(keyRegex, " $1").lowercase().replaceFirstChar { it.uppercase() }
                "<${MochaColorScheme.Lavender.argb}>$s <gray>➤ ${if (b) "<red>Disabled" else "<green>Enabled"}".mod()
            }

            "simulate" / "chat" / bool("actionbar") / greedyString("message") {
                val actionBar = bool("actionbar")
                val message = string("message")

                if (actionBar) MessageEvent.ActionBar(message.parse()).post()
                else MessageEvent.Chat.Receive(message.parse()).post()

                "<gray>Simulated ($actionBar): <red>$message".mod()
            }

            "clear" / "chat" {
                MinecraftChat.instance.clearMessages(false)
            }
        }
    }
}
