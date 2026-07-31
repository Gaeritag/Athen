package foo.starred.athen.events

import foo.starred.athen.events.core.Event

sealed class GameEvent : Event() {
    data object Start : GameEvent()

    data object Stop : GameEvent()
}
