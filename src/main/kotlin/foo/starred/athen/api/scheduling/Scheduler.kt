package foo.starred.athen.api.scheduling

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.handlers.time.AbstractChronos

@Priority
object Scheduler : AbstractChronos() {
    init {
        on<TickEvent.Client.Start> {
            client0()
        }

        on<TickEvent.Client.End> {
            client1()
        }

        on<TickEvent.Server> {
            server()
        }
    }
}