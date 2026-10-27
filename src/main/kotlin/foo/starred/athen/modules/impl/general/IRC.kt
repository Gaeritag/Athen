@file:Suppress("Unused")

package foo.starred.athen.modules.impl.general

import com.google.gson.JsonParser
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.network.websocket.base.IWebSocket
import foo.starred.athen.api.network.websocket.data.SocketPacket
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.command
import foo.starred.kbus.extensions.runWhen
import foo.starred.kommand.IKommand
import foo.starred.kommand.scopes.KommandCommandScope
import foo.starred.kontext.abstraction.chat.generic.MinecraftChat
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.text.parser.impl.parse
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.protocol.game.ServerboundChatPacket

@LoadableAthenModule
object IRC : Module(
    "IRC",
    "Enables the IRC by default on launch if the module is enabled.",
    ConfigCategory.GENERAL
), IWebSocket, IKommand<FabricClientCommandSource> {
    override val loader: KommandCommandScope<FabricClientCommandSource> = KommandCommandScope()

    private val _unused by config.information("Run <red>\"/athen irc help\" <r>to view all commands!")
    private val help by config.switch("Help message", true)
    private val format0 by config.input("Message format", "<#A6E3A1>#name <dark_gray>➤ <white>#message")
    private val discord by config.switch("Discord IRC", true)
    private val format1 by config.input("Discord format", "<#A6E3A1>#name <dark_gray>➤ <white>#message")

    private val ob: Observable<Boolean> = Observable(false)
    private var cc: String = ""

    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            loader.register(dispatcher)
        }

        command("airc") {
            greedyString("message") {
                if (!auth) return@greedyString er0()
                if (!enabled) return@greedyString er1()

                send(string("message"))
            }

            "toggle" {
                val b = !ob.value
                ob.value = b
                "Send all messages to IRC <gray>➤ ${if (b) "<green>Enabled" else "<red>Disabled"}".mod()
            }

            "help" {
                help()
            }
        }

        command {
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
                    "<gray>Joined channel <aqua>#$channel".mod()
                    if (help) "<gray>Need help? Run <red>\"/athen irc help\"<r>!".mod()
                }

                SocketPacket.IRC.ClientBound.Left.id -> {
                    if (channel == null) return@on
                    if (cc == channel) cc = "general"

                    "<gray>Left channel <aqua>#$channel".mod()
                }

                SocketPacket.IRC.ClientBound.Chat.id -> {
                    if (channel == null) return@on
                    if (name == null) return@on
                    if (body == null) return@on
                    if (name == client.user.name) return@on
                    if (name == "[Discord]") return@on

                    "<dark_gray>[<aqua>#$channel<dark_gray>]".format0(name, body).mod()
                }

                SocketPacket.IRC.ClientBound.Discord.id -> {
                    if (!discord) return@on
                    if (name == null) return@on
                    if (body == null) return@on

                    "<dark_gray>[<aqua>Discord<dark_gray>]".format1(name, body).mod()
                }

                SocketPacket.IRC.ClientBound.Error.id -> {
                    "<red>IRC error: <gray>$body".mod(MessagePrefixType.ERROR)
                }

                SocketPacket.IRC.ClientBound.Warn.id -> {
                    "<yellow>IRC: <gray>$body".mod(MessagePrefixType.ERROR)
                }

                SocketPacket.IRC.ClientBound.List.id -> {
                    if (body == null) return@on

                    val ch = runCatching {
                        JsonParser.parseString(body).asJsonArray.map {
                            val obj = it.asJsonObject
                            "${obj.get("first").asString} (${obj.get("second").asInt})"
                        }.sortedWith(compareBy({ if (it.startsWith("general ")) 0 else 1 }, { it }))
                    }.getOrNull() ?: return@on

                    if (ch.isEmpty()) "<gray>No active channels.".mod()
                    else "<gray>Active channels: <aqua>${ch.joinToString("<dark_gray>, <aqua>") { ch -> "#$ch" }}".mod()
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
        if ("@everyone" in body) return "Please don't ping everyone...".mod()
        if ("@here" in body) return "Please don't ping every online member...".mod()

        `socket$send`(SocketPacket.IRC.ServerBound.Chat.id, "b" to body)
        "<dark_gray>[<aqua>#$cc<dark_gray>]".format0(client.user.name, body).parse(true).mod()
    }

    private fun String.format0(n: String, b: String): String {
        return "$this " + format0.replace("#name", n).replace("#message", b)
    }

    private fun String.format1(n: String, b: String): String {
        return "$this " + format1.replace("#name", n).replace("#message", b)
    }

    private fun help() {
        val a = ("<dark_gray>" + MinecraftChat.repeat("-")).parse()
        val c = MochaColorScheme.Green.argb

        MinecraftMessages.display(a)
        MinecraftMessages.display("<red>" + MinecraftChat.center("Athen IRC"))
        MinecraftMessages.display(a)

        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc create [channel] [pin <gray>- optional<$c>]")
        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc join [channel] [pin <gray>- optional<$c>]")
        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc leave <gray>- leave channel")
        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc pin [pin] <gray>- sets a pin")
        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc chat [message]")
        MinecraftMessages.display(" <dark_gray>- <$c>/athen irc list <gray>- list channels")

        MinecraftMessages.display(a)

        MinecraftMessages.display(" <dark_gray>- <$c>/airc [message] <gray>- send message alias")
        MinecraftMessages.display(" <dark_gray>- <$c>/airc toggle <gray>- send all messages to irc")

        MinecraftMessages.display(a)
    }

    private fun er0() {
        "Not connected to IRC! Use <yellow>/athen ws connect".mod(MessagePrefixType.ERROR)
    }

    private fun er1() {
        "<red>IRC module not enabled!".mod(MessagePrefixType.ERROR)
    }
}
