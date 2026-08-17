package foo.starred.athen.config.dsl.impl.builders.group

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigGroupElementData
import foo.starred.athen.config.dsl.base.ConfigScope
import foo.starred.athen.config.dsl.base.ElementBuilder
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.snowbird.handlers.Observable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigGroupBuilder(
    override val builder: ConfigMainBuilder,
    private val name: String,
    parent0: String? = null
) : ElementBuilder<ConfigGroupBuilder>(parent0), ConfigScope, ReadOnlyProperty<Any?, ConfigGroupBuilder> {
    private val state = Observable(false)

    override val parent: String
        get() = key

    lateinit var key: String
        private set

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, ConfigGroupBuilder> {
        key = "${builder.configKey}.expandable_${property.name}"

        val data = ConfigGroupElementData(name, key, super<ElementBuilder>.parent, description)
        builder.feature.option(data)

        ConfigManager.observe(key) { state.value = it as? Boolean ?: false }
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): ConfigGroupBuilder {
        return this
    }
}