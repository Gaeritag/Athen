package foo.starred.athen.modules

import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.annotations.Redstone
import foo.starred.athen.api.dungeon.DungeonAPI
import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.config.Category
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.Event
import foo.starred.athen.events.core.runWhen
import foo.starred.snowbird.api.ALWAYS_TRUE
import foo.starred.snowbird.api.data.Observable
import foo.starred.snowbird.api.data.Observable.Companion.and
import foo.starred.snowbird.api.lazy.CallbackLazy
import foo.starred.snowbird.utils.toCamelCase
import net.minecraft.network.protocol.Packet
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class Module(
    name: String,
    description: String,
    category: Category,
    default: Boolean = false
) {
    private val _location: Observable<Boolean> = run {
        val onlyIn = this::class.findAnnotation<OnlyIn>() ?: return@run ALWAYS_TRUE
        when {
            onlyIn.floors.isNotEmpty() -> DungeonAPI.floor.map { it in onlyIn.floors }
            onlyIn.areas.isNotEmpty() -> LocationAPI.area.map { it in onlyIn.areas }
            onlyIn.islands.isNotEmpty() -> LocationAPI.island.map { it in onlyIn.islands }
            onlyIn.skyblock -> LocationAPI.isOnSkyBlock
            else -> ALWAYS_TRUE
        }
    }

    val observable: Observable<Boolean> by CallbackLazy(::fn0) {
        if (redstone) ALWAYS_TRUE else config.state and _location
    }

    val configKey: String =
        name.toCamelCase()

    val redstone: Boolean =
        this::class.hasAnnotation<Redstone>()

    val config: ConfigMainBuilder =
        ConfigMainBuilder(configKey, name, description, category, default).also { it.module = this }

    var enabled: Boolean = false
        private set

    protected inline fun <reified T : Event> on(
        priority: Int = 0,
        noinline handler: T.() -> Unit
    ) = foo.starred.athen.events.core.on<T>(priority, handler).runWhen(observable)

    protected inline fun <reified E : PacketEvent, reified P : Packet<*>> on(
        priority: Int = 0,
        noinline handler: P.(E) -> Unit
    ) = foo.starred.athen.events.core.on<E, P>(priority, handler).runWhen(observable)

    private fun fn0() {
        enabled = observable.value
        observable.onChange { enabled = it }
    }
}
