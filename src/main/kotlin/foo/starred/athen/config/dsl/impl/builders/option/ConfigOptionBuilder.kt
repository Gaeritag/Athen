package foo.starred.athen.config.dsl.impl.builders.option

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.base.IConfigElementData
import foo.starred.athen.config.dsl.base.ElementBuilder
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.snowbird.handlers.Observable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigOptionBuilder<T>(
    val builder: ConfigMainBuilder,
    private val default: T,
    private val data: IConfigElementData,
    parent: String? = null
) : ElementBuilder<ConfigOptionBuilder<T>>(parent), ReadOnlyProperty<Any?, T> {
    private val calls = mutableListOf<(ConfigOptionBuilder<T>) -> Unit>()
    private var bool = false

    val state = Observable(default)

    val value: T
        get() = state.value

    lateinit var key: String
        private set

    fun unique(key: String): ConfigOptionBuilder<T> {
        this.key = "${builder.configKey}.$key"
        fn()
        return this
    }

    fun resolve(block: (ConfigOptionBuilder<T>) -> Unit) = apply {
        calls += block
    }
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        if (!::key.isInitialized) key = "${builder.configKey}.${property.name}"
        fn()
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return state.value
    }

    private fun fn() {
        if (bool) return

        bool = true
        builder.feature.option(data.with(key, parent, description))

        ConfigManager.observe(key) {
            @Suppress("UNCHECKED_CAST")
            state.value = when (default) {
                is Int -> (it as? Number)?.toInt()
                is Double -> (it as? Number)?.toDouble()
                is Float -> (it as? Number)?.toFloat()
                is Long -> (it as? Number)?.toLong()
                else -> it
            } as? T ?: default
        }

        for (c in calls) c(this)
    }
}