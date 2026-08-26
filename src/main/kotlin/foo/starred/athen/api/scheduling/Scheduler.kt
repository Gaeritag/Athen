package foo.starred.athen.api.scheduling

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.scheduling.scheduler.impl.AbstractScheduler

@Priority
object Scheduler : AbstractScheduler() {
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
