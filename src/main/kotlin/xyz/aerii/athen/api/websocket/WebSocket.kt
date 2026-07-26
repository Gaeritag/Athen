package xyz.aerii.athen.api.websocket

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelAndJoin
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
import xyz.aerii.library.utils.safely
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Priority
object WebSocket {
    private val http = HttpClient(CIO) {
        install(HttpTimeout) {
            connectTimeoutMillis = 10000
        }

        install(WebSockets) {
            pingIntervalMillis = 15_000
            maxFrameSize = Long.MAX_VALUE
        }
    }

    private var ws: WebSocketSession? = null
    private var rc: Task? = null
    private val ch: Channel<JsonObject> = Channel(Channel.UNLIMITED)
    private var job: Job? = null

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
        auth = false

        val s = UUID.randomUUID().toString()

        try {
            client.services().sessionService().joinServer(client.user.profileId, client.user.accessToken, s)
        } catch (e: Exception) {
            Athen.LOGGER.error("Failed to authenticate with Mojang!", e)
            "Failed to authenticate with Mojang! This shouldn't happen, please message @skies.starred on discord with a copy of your logs.".modMessage(Typo.PrefixType.ERROR)
            return
        }

        job = SCOPE.launch {
            try {
                http.webSocket(wsUrl) {
                    ws = this

                    send(JsonObject().apply {
                        addProperty("t", SocketPacket.WebSocket.ServerBound.Auth.id)
                        addProperty("n", name)
                        addProperty("s", s)
                    }.toString())

                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        val json = frame.readText()

                        runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull()?.let { j ->
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
                }
            } catch (e: Exception) {
                auth = false
                Athen.LOGGER.error("Websocket connection failed/closed: ${e.message}")
                if (ws != null) fn0()
            }
        }
    }

    fun close() {
        val ows = ws
        val ojob = job
        ws = null
        job = null
        rc?.cancel()
        auth = false

        SCOPE.launch {
            safely {
                ows?.close(CloseReason(CloseReason.Codes.NORMAL, ""))
                ojob?.cancelAndJoin()
            }
        }
    }

    fun send(json: JsonObject) {
        ch.trySend(json)
    }

    private fun fn0() {
        rc?.cancel()
        rc = Chronos.repeat(15.seconds) { connect() }
    }
}