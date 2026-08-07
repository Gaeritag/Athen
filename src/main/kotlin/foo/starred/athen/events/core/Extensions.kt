@file:Suppress("NOTHING_TO_INLINE")

package foo.starred.athen.events.core

import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.snowbird.handlers.Observable
import net.minecraft.network.protocol.Packet

inline fun <reified T : Event> on(
    priority: Int = 0,
    noinline handler: T.() -> Unit
) = Node(T::class.java, handler, priority).apply { register() }

inline fun <reified E : PacketEvent, reified P : Packet<*>> on(
    priority: Int = 0,
    noinline handler: P.(E) -> Unit
): Node<*> {
    return on<E>(priority) {
        (packet as? P)?.handler(this)
    }
}

fun Node<*>.runWhen(state: Observable<Boolean>) = apply {
    if (overridden) return@apply
    if (LocationEvent.Server::class.java.isAssignableFrom(eventClass)) return@apply
    if (SlayerEvent.Reset::class.java.isAssignableFrom(eventClass)) return@apply
    add(state)
}

fun Node<*>.override(state: Observable<Boolean>) = apply {
    overridden = true
    conditions.clear()
    add(state)
}

fun Node<*>.override() = apply {
    overridden = true
    conditions.clear()
    register()
}

private fun Node<*>.add(state: Observable<Boolean>) = apply {
    conditions.add(state)
    state.onChange { evaluate() }
    evaluate()
}