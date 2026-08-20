package foo.starred.athen.api.network.websocket

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import foo.starred.athen.Athen
import foo.starred.athen.Athen.SCOPE
import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.GameEvent
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.Dev
import foo.starred.athen.utils.command
import foo.starred.athen.utils.wsUrl
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.name
import foo.starred.snowbird.handlers.time.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.net.URI
import java.net.http.HttpClient
import java.util.*
import java.util.concurrent.CompletionStage
import kotlin.time.Duration.Companion.seconds
import java.net.http.WebSocket as JavaWebSocket

@Priority
object WebSocket {
    private val http = HttpClient.newHttpClient()

    private var ws: JavaWebSocket? = null
    private var rc: Task? = null
    private val out: Channel<JsonObject> = Channel(Channel.UNLIMITED)
    private val inn: Channel<String> = Channel(Channel.UNLIMITED)
    private val buffer = StringBuilder()

    @Volatile
    private var manual = false

    @Volatile
    var auth = false
        private set

    init {
        command {
            "ws" / "connect" {
                "<gray>Connecting to WebSocket...".mod()
                SCOPE.launch { connect() }
            }

            "ws" / "disconnect" {
                if (!auth) return@invoke "Not connected to WebSocket!".mod(MessagePrefixType.ERROR)
                close()
                "<gray>Disconnected from WebSocket.".mod()
            }
        }

        SCOPE.launch {
            for (c in out) {
                c.addProperty("n", name)
                ws?.sendText(c.toString(), true)
            }
        }

        SCOPE.launch {
            inn.consumeEach {
                fn(it)
            }
        }

        on<GameEvent.Stop> {
            close()
        }

        connect()
    }

    fun connect() {
        close()
        manual = false
        auth = false

        val server = UUID.randomUUID().toString()

        try {
            client.services().sessionService().joinServer(client.user.profileId, client.user.accessToken, server)
        } catch (e: Exception) {
            Athen.LOGGER.error("Failed to authenticate with Mojang!", e)
            "Failed to authenticate with Mojang! This shouldn't happen, please message @skies.starred on discord with a copy of your logs.".mod(MessagePrefixType.ERROR)
            return
        }

        http.newWebSocketBuilder()
            .buildAsync(URI.create(wsUrl), object : JavaWebSocket.Listener {
                override fun onOpen(webSocket: JavaWebSocket) {
                    webSocket.sendText(JsonObject().apply {
                        addProperty("t", SocketPacket.WebSocket.ServerBound.Auth.id)
                        addProperty("n", name)
                        addProperty("s", server)
                    }.toString(), true)
                    webSocket.request(1)
                }

                override fun onText(webSocket: JavaWebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
                    buffer.append(data)

                    if (last) {
                        inn.trySend(buffer.toString())
                        buffer.clear()
                    }

                    webSocket.request(1)
                    return null
                }

                override fun onClose(webSocket: JavaWebSocket, statusCode: Int, reason: String): CompletionStage<*>? {
                    fail(reason)
                    return null
                }

                override fun onError(webSocket: JavaWebSocket, error: Throwable) {
                    fail(error.message)
                }
            })
            .whenComplete { socket, err ->
                if (err != null) {
                    fail(err.message)
                    return@whenComplete
                }
                ws = socket
            }
    }

    fun close() {
        manual = true
        rc?.cancel()
        auth = false
        ws?.sendClose(JavaWebSocket.NORMAL_CLOSURE, "Closed by client")
        ws = null
    }

    fun send(json: JsonObject) {
        out.trySend(json)
    }

    private fun fn(text: String) {
        val json = runCatching { JsonParser.parseString(text).asJsonObject }.getOrNull() ?: return
        val t = json.get("t")?.asInt ?: return
        val c = json.get("c")?.asString
        val n = json.get("n")?.asString
        val b = json.get("b")?.asString

        when (t) {
            SocketPacket.WebSocket.ClientBound.AuthSuccess.id -> {
                auth = true
                Athen.LOGGER.info("Websocket authenticated as $n")
                if (Dev.debug) "<green>Connected to Websocket as <white>$n".mod()
            }

            SocketPacket.WebSocket.ClientBound.AuthError.id -> {
                Athen.LOGGER.error("Websocket authentication failed: $b")
                "<red>Failed to authenticate! <gray>Error: $b".mod(MessagePrefixType.ERROR)
            }

            SocketPacket.WebSocket.ClientBound.Error.id -> {
                Athen.LOGGER.error("Websocket error: $b")
                "<red>WS error: <gray>$b".mod(MessagePrefixType.ERROR)
            }

            SocketPacket.WebSocket.ClientBound.Warn.id -> {
                if (b != null) "<yellow>$b".mod(MessagePrefixType.ERROR)
            }

            else -> {
                InternalEvent.WebSocket.Message(t, b, c, n).post()
            }
        }
    }

    private fun fail(reason: String?) {
        auth = false
        if (manual) return

        Athen.LOGGER.error("Websocket connection failed/closed: $reason")
        rc?.cancel()
        rc = Scheduler.repeat(15.seconds) { connect() }
    }
}