package foo.starred.athen.events

import foo.starred.athen.events.core.Event
import net.minecraft.network.chat.Component

sealed class ScoreboardEvent {
    data class UpdateTitle(
        val old: String?,
        val new: String
    ) : Event()

    data class Update(
        val old: List<String>,
        val new: List<String>,
        val oldComponents: List<Component>,
        val newComponents: List<Component>,
    ) : Event() {
        val added: List<String> = new - old.toSet()
        val removed: List<String> = old - new.toSet()

        val addedComponents: List<Component> = newComponents - oldComponents.toSet()
        val removedComponents: List<Component> = oldComponents - newComponents.toSet()
    }
}
