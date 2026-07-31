@file:Suppress("Unused")

package foo.starred.athen.modules.impl.general

import com.google.gson.JsonParser
import net.minecraft.network.protocol.game.ServerboundChatPacket
import foo.starred.athen.Athen
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.websocket.SocketPacket
import foo.starred.athen.api.websocket.base.IWebSocket
import foo.starred.athen.config.Category
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.handlers.Typo
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.snowbird.api.center
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.name
import foo.starred.snowbird.api.repeat
import foo.starred.snowbird.handlers.Observable
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.kommand.ICommand

@Load
object IRC : Module(
    "IRC",
    "Enables the IRC by default on launch if the module is enabled.",
    Category.GENERAL,
    true
), IWebSocket, ICommand {
    private val _unused by config.textParagraph("Run <red>\"/athen irc help\" <r>to view all commands!")
    private val help by config.switch("Help message", true)
    private val format0 by config.textInput("Message format", "<#A6E3A1>#name <dark_gray>➤ <white>#message")
    private val discord by config.switch("Discord IRC", true)
    private val format1 by config.textInput("Discord format", "<#A6E3A1>#name <dark_gray>➤ <white>#message").dependsOn { discord }

    private val ob: Observable<Boolean> = Observable(false)
    private var cc: String = ""

    init {
        command("airc") {
            greedyString("message") {
                if (!auth) return@greedyString er0()
                if (!enabled) return@greedyString er1()

                send(string("message"))
            }

            "toggle" {
                val b = !ob.value
                ob.value = b
                "Send all messages to IRC <gray>➤ ${if (b) "<green>Enabled" else "<red>Disabled"}".parse().modMessage()
            }

            "help" {
                help()
            }
        }

        command(Athen.modId) {
            "irc" / "chat" / greedyString("message") {
                if (!auth) return@greedyString er0()
                if (!enabled) return@greedyString er1()

                send(string("message"))
            }

            "irc" / "create" / string("channel") {
                if (!auth) return@string er0()
                if (!enabled) return@string er1()

                create(string("channel"))
            }

            "irc" / "create" / string("channel") / string("pin") {
                if (!auth) return@string er0()
                if (!enabled) return@string er1()

                create(string("channel"),string("pin"))
            }

            "irc" / "pin" / string("pin") {
                if (!auth) return@string er0()
                if (!enabled) return@string er1()

                pin(string("pin"))
            }

            "irc" / "join" / string("channel") {
                if (!auth) return@string er0()
                if (!enabled) return@string er1()

                join(string("channel"))
            }

            "irc" / "join" / string("channel") / string("pin") {
                if (!auth) return@string er0()
                if (!enabled) return@string er1()

                join(string("channel"), string("pin"))
            }

            "irc" / "leave" {
                if (!auth) return@invoke er0()

                leave()
            }

            "irc" / "list" {
                if (!auth) return@invoke er0()

                list()
            }

            "irc" / "help" {
                help()
            }
        }

        on<PacketEvent.Send, ServerboundChatPacket> {
            if (message.startsWith('/')) return@on
            send(message)
            it.cancel()
        }.runWhen(ob)

        on<InternalEvent.WebSocket.Message> {
            if (id !in SocketPacket.IRC.ClientBound.all) return@on

            when (id) {
                SocketPacket.IRC.ClientBound.Join.id -> {
                    if (channel == null) return@on
                    if (channel == cc) return@on

                    cc = channel
                    "<gray>Joined channel <aqua>#$channel".parse().modMessage()
                    if (help) "<gray>Need help? Run <red>\"/athen irc help\"<r>!".parse().modMessage()
                }

                SocketPacket.IRC.ClientBound.Left.id -> {
                    if (channel == null) return@on
                    if (cc == channel) cc = "general"

                    "<gray>Left channel <aqua>#$channel".parse().modMessage()
                }

                SocketPacket.IRC.ClientBound.Chat.id -> {
                    if (channel == null) return@on
                    if (name == null) return@on
                    if (body == null) return@on
                    if (name == client.user.name) return@on
                    if (name == "[Discord]") return@on

                    "<dark_gray>[<aqua>#$channel<dark_gray>]".format0(name, body).parse().modMessage()
                }

                SocketPacket.IRC.ClientBound.Discord.id -> {
                    if (!discord) return@on
                    if (name == null) return@on
                    if (body == null) return@on

                    "<dark_gray>[<aqua>Discord<dark_gray>]".format1(name, body).parse().modMessage()
                }

                SocketPacket.IRC.ClientBound.Error.id -> {
                    "<red>IRC error: <gray>$body".parse().modMessage(Typo.PrefixType.ERROR)
                }

                SocketPacket.IRC.ClientBound.Warn.id -> {
                    "<yellow>IRC: <gray>$body".parse().modMessage(Typo.PrefixType.ERROR)
                }

                SocketPacket.IRC.ClientBound.List.id -> {
                    if (body == null) return@on

                    val ch = runCatching {
                        JsonParser.parseString(body).asJsonArray.map {
                            val obj = it.asJsonObject
                            "${obj.get("first").asString} (${obj.get("second").asInt})"
                        }.sortedWith(compareBy({ if (it.startsWith("general ")) 0 else 1 }, { it }))
                    }.getOrNull() ?: return@on

                    if (ch.isEmpty()) "<gray>No active channels.".parse().modMessage()
                    else "<gray>Active channels: <aqua>${ch.joinToString("<dark_gray>, <aqua>") { ch -> "#$ch" }}".parse().modMessage()
                }
            }
        }
    }

    private fun create(channel: String, pin: String? = null) {
        `socket$send`(SocketPacket.IRC.ServerBound.Create.id, "c" to channel, "p" to pin)
    }

    private fun pin(pin: String) {
        `socket$send`(SocketPacket.IRC.ServerBound.Pin.id, "p" to pin)
    }

    private fun join(channel: String, pin: String? = null) {
        `socket$send`(SocketPacket.IRC.ServerBound.Join.id, "c" to channel, "p" to pin)
    }

    private fun leave() {
        `socket$send`(SocketPacket.IRC.ServerBound.Leave.id)
    }

    private fun list() {
        `socket$send`(SocketPacket.IRC.ServerBound.List.id)
    }

    private fun send(body: String) {
        if ("@everyone" in body) return "Please don't ping everyone...".modMessage()
        if ("@here" in body) return "Please don't ping every online member...".modMessage()

        `socket$send`(SocketPacket.IRC.ServerBound.Chat.id, "b" to body)
        "<dark_gray>[<aqua>#$cc<dark_gray>]".format0(name, body).parse(true).modMessage()
    }

    private fun String.format0(n: String, b: String): String {
        return "$this " + format0.replace("#name", n).replace("#message", b)
    }

    private fun String.format1(n: String, b: String): String {
        return "$this " + format1.replace("#name", n).replace("#message", b)
    }

    private fun help() {
        val a = ("<dark_gray>" + ("-".repeat())).parse()
        val b = Athen.modId
        val c = Catppuccin.Mocha.Green.argb

        a.lie()
        ("<red>" + ("Athen IRC".center())).parse().lie()
        a.lie()

        " <dark_gray>- <$c>/$b irc create [channel] [pin <gray>- optional<$c>]".parse().lie()
        " <dark_gray>- <$c>/$b irc join [channel] [pin <gray>- optional<$c>]".parse().lie()
        " <dark_gray>- <$c>/$b irc leave <gray>- leave channel".parse().lie()
        " <dark_gray>- <$c>/$b irc pin [pin] <gray>- sets a pin".parse().lie()
        " <dark_gray>- <$c>/$b irc chat [message]".parse().lie()
        " <dark_gray>- <$c>/$b irc list <gray>- list channels".parse().lie()

        a.lie()

        " <dark_gray>- <$c>/airc [message] <gray>- send message alias".parse().lie()
        " <dark_gray>- <$c>/airc toggle <gray>- send all messages to irc".parse().lie()

        a.lie()
    }

    private fun er0() {
        "Not connected to IRC! Use <yellow>/${Athen.modId} ws connect".parse().modMessage(Typo.PrefixType.ERROR)
    }

    private fun er1() {
        "<red>IRC module not enabled!".parse().modMessage(Typo.PrefixType.ERROR)
    }
}