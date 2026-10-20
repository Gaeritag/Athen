package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import net.minecraft.network.chat.Component

sealed class ScoreboardEvent {
    data class Update(
        val old: List<String>,
        val new: List<String>,
        val oldComponents: List<Component>,
        val newComponents: List<Component>,
    ) : AthenEvent() {
        val added: List<String> by lazy(LazyThreadSafetyMode.NONE) {
            new - old.toSet()
        }

        val removed: List<String> by lazy(LazyThreadSafetyMode.NONE) {
            old - new.toSet()
        }
    }
}
