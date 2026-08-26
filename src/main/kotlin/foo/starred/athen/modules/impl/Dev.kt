package foo.starred.athen.modules.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.storage.JsonStore
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.command
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.text.parser.impl.parse

@Load
object Dev {
    private val r = Regex("(?<!^)([A-Z])")

    @JvmStatic
    val file = JsonStore("main/Dev")

    @JvmStatic
    var lastVersion: String by file.string("lastVersion")

    @JvmStatic
    var lastBroadcast: String by file.string("lastBroadcast")

    @JvmStatic
    var debug: Boolean by file.boolean("enabled")

    init {
        command {
            "toggle" / "dev" {
                debug = !debug
                val a = if (debug) "<green>Enabled" else "<red>Disabled"

                "Debug mode is now: $a<r>.".mod()
            }

            "toggle" / "feature" / string("key") {
                val key = string("key")

                val b = ConfigManager.get(key) as? Boolean ?: return@string "Not a valid feature!".mod()
                ConfigManager.update(key, !b)

                val s = key.replace(r, " $1").lowercase().replaceFirstChar { it.uppercase() }
                "<${Mocha.Lavender.argb}>$s <gray>➤ ${if (b) "<red>Disabled" else "<green>Enabled"}".mod()
            }

            "simulate" / "chat" / bool("actionbar") / greedyString("message") {
                val actionBar = bool("actionbar")
                val message = string("message")

                if (actionBar) MessageEvent.ActionBar(message.parse()).post()
                else MessageEvent.Chat.Receive(message.parse()).post()

                "<gray>Simulated ($actionBar): <red>$message".mod()
            }

            "clear" / "chat" {
                //~ if >= 26.2 'client.gui.chat' -> 'client.gui.hud.chat'
                client.gui.chat.clearMessages(false)
            }
        }
    }
}
