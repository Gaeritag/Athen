@file:Suppress("NOTHING_TO_INLINE")

package foo.starred.athen.api.network.http

object AthenAPI {
    inline fun websocket(): String {
        return "wss://api.starred.foo/ws"
    }

    inline fun data(string: String): String {
        return "https://data.starred.foo/$string"
    }

    inline fun get(string: String): String {
        return "https://api.starred.foo/$string"
    }
}
