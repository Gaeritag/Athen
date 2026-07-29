package xyz.aerii.athen.utils

const val wsUrl: String = "wss://api.starred.foo/ws"
const val apiUrl: String = "https://api.starred.foo"
const val dataUrl: String = "https://data.starred.foo"

inline val String.api: String
    get() = "$apiUrl/$this"

inline val String.data: String
    get() = "$dataUrl/$this"