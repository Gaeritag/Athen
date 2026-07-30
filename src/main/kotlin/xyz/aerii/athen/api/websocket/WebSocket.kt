package xyz.aerii.athen.api.websocket

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import xyz.aerii.athen.Athen
import xyz.aerii.athen.Athen.SCOPE
import xyz.aerii.athen.annotations.Priority
import xyz.aerii.athen.events.InternalEvent
import xyz.aerii.athen.handlers.Chronos
import xyz.aerii.athen.handlers.Typo
import xyz.aerii.athen.handlers.Typo.modMessage
import xyz.aerii.athen.modules.impl.Dev
import xyz.aerii.athen.utils.command
import xyz.aerii.athen.utils.wsUrl
import xyz.aerii.library.api.client
import xyz.aerii.library.api.name
import xyz.aerii.library.handlers.parser.parse
import xyz.aerii.library.handlers.time.Task
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@Priority
object WebSocket {
    private val http = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).pingInterval(15, TimeUnit.SECONDS).build()

    private var ws: WebSocket? = null
    private var rc: Task? = null
    private val ch: Channel<JsonObject> = Channel(Channel.UNLIMITED)

    @Volatile
    private var manual = false

    @Volatile
    var auth = false
        private set

    init {
        command {
            "ws" / "connect" {
                "<gray>Connecting to WebSocket...".parse().modMessage()
                SCOPE.launch { connect() }
            }

            "ws" / "disconnect" {
                if (!auth) return@invoke "Not connected to WebSocket!".modMessage(Typo.PrefixType.ERROR)
                SCOPE.launch { close() }
                "<gray>Disconnected from WebSocket.".parse().modMessage()
            }
        }

        SCOPE.launch {
            for (c in ch) {
                c.addProperty("n", name)
                ws?.send(c.toString())
            }
        }

        connect()
    }

    fun connect() {
        close()
        manual = false
        auth = false

        val s = UUID.randomUUID().toString()

        try {
            client.services().sessionService().joinServer(client.user.profileId, client.user.accessToken, s)
        } catch (e: Exception) {
            Athen.LOGGER.error("Failed to authenticate with Mojang!", e)
            "Failed to authenticate with Mojang! This shouldn't happen, please message @skies.starred on discord with a copy of your logs.".modMessage(Typo.PrefixType.ERROR)
            return
        }

        ws = http.newWebSocket(Request.Builder().url(wsUrl).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(JsonObject().apply {
                    addProperty("t", SocketPacket.WebSocket.ServerBound.Auth.id)
                    addProperty("n", name)
                    addProperty("s", s)
                }.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching { JsonParser.parseString(text).asJsonObject }.getOrNull()?.let { j ->
                    val t = j.get("t")?.asInt ?: return@let
                    val c = j.get("c")?.asString
                    val n = j.get("n")?.asString
                    val b = j.get("b")?.asString

                    when (t) {
                        SocketPacket.WebSocket.ClientBound.AuthSuccess.id -> {
                            auth = true
                            Athen.LOGGER.info("Websocket authenticated as $n")
                            if (Dev.debug) "<green>Connected to Websocket as <white>$n".parse().modMessage()
                        }

                        SocketPacket.WebSocket.ClientBound.AuthError.id -> {
                            Athen.LOGGER.error("Websocket authentication failed: $b")
                            "<red>Failed to authenticate! <gray>Error: $b".parse().modMessage(Typo.PrefixType.ERROR)
                        }

                        SocketPacket.WebSocket.ClientBound.Error.id -> {
                            Athen.LOGGER.error("Websocket error: $b")
                            "<red>WS error: <gray>$b".parse().modMessage(Typo.PrefixType.ERROR)
                        }

                        SocketPacket.WebSocket.ClientBound.Warn.id -> {
                            if (b != null) "<yellow>$b".parse().modMessage(Typo.PrefixType.ERROR)
                        }

                        else -> {
                            InternalEvent.WebSocket.Message(t, b, c, n).post()
                        }
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                auth = false
                if (manual) return
                Athen.LOGGER.error("Websocket connection closed: $reason")
                fn0()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                auth = false
                if (manual) return
                Athen.LOGGER.error("Websocket connection failed/closed: ${t.message}")
                fn0()
            }
        })
    }

    fun close() {
        manual = true
        rc?.cancel()
        auth = false
        ws?.close(1000, "Closed by client")
        ws = null
    }

    fun send(json: JsonObject) {
        ch.trySend(json)
    }

    private fun fn0() {
        rc?.cancel()
        rc = Chronos.repeat(15.seconds) { connect() }
    }
}