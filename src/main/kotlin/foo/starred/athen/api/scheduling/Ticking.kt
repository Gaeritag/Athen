package foo.starred.athen.api.scheduling

import foo.starred.snowbird.api.scheduling.tickable.AbstractTickable

class Ticking<T>(ticks: Int = 1, block: () -> T) : AbstractTickable<T>(ticks, { Scheduler.ticks.client }, block)
