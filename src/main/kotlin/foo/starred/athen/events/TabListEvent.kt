package foo.starred.athen.events

import net.minecraft.network.chat.Component
import foo.starred.athen.events.core.Event

sealed class TabListEvent {
    data class Change(
        val old: List<List<String>>,
        val new: List<List<Component>>,
    ) : Event()
}
