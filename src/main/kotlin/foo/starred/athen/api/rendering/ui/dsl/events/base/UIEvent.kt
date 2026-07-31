@file:Suppress("Unused")

package foo.starred.athen.api.rendering.ui.dsl.events.base

abstract class UIEvent {
    @Volatile
    var cancelled = false
        private set

    fun cancel() {
        cancelled = true
    }
}