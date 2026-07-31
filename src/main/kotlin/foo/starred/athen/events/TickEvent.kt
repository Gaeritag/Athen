package foo.starred.athen.events

import foo.starred.athen.events.core.Event
import foo.starred.athen.handlers.Chronos

sealed class TickEvent {
    sealed class Client {
        data object Start : Event()

        data object End : Event() {
            val ticks: Int
                get() = Chronos.ticks.client
        }
    }

    data object Server : Event() {
        val ticks: Int
            get() = Chronos.ticks.server
    }
}
