package foo.starred.athen.handlers

import foo.starred.snowbird.handlers.delegate.AbstractTickable

class Ticking<T>(ticks: Int = 1, block: () -> T) : AbstractTickable<T>(ticks, { Chronos.ticks.client }, block)