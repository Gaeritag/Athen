@file:Suppress("PropertyName")

package foo.starred.athen.modules

import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.node.KBusNode
import foo.starred.kbus.extensions.runWhen
import foo.starred.snowbird.api.data.Observable
import foo.starred.snowbird.api.data.Observable.Companion.and
import foo.starred.snowbird.utils.toCamelCase
import net.minecraft.network.protocol.Packet

open class Module(
    name: String,
    description: String,
    category: ConfigCategory,
    default: Boolean = false
) {
    private val _location: Observable<Boolean> = OnlyIn.get(this::class)

    val _key: String = name.toCamelCase()
    val config: ConfigMainBuilder = ConfigMainBuilder(_key, name, description, category, default).also { it.module = this }
    val observable: Observable<Boolean> = config.state and _location

    val enabled: Boolean
        get() = observable.value

    protected inline fun <reified T : AthenEvent> on(priority: Int = 0, noinline handler: T.() -> Unit): KBusNode<*> {
        return foo.starred.athen.events.core.on<T>(priority, handler).runWhen(observable)
    }

    protected inline fun <reified E : PacketEvent, reified P : Packet<*>> on(priority: Int = 0, noinline handler: P.(E) -> Unit): KBusNode<*> {
        return foo.starred.athen.events.core.on<E, P>(priority, handler).runWhen(observable)
    }
}
