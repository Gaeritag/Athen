package foo.starred.athen.events

import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.core.Event

sealed class TickEvent {
    sealed class Client {
        data object Start : Event()

        data object End : Event() {
            val ticks: Int
                get() = Scheduler.ticks.client
        }
    }

    data object Server : Event() {
        val ticks: Int
            get() = Scheduler.ticks.server
    }
}
