package foo.starred.athen.events

import foo.starred.athen.events.core.Event
import net.minecraft.network.chat.Component

sealed class TabListEvent {
    data class Change(
        val old: List<List<String>>,
        val new: List<List<Component>>,
    ) : Event()
}
