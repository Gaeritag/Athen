package foo.starred.athen.config.dsl.impl.builders.config

import foo.starred.athen.Athen
import foo.starred.athen.config.Category
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.dsl.base.ConfigScope
import foo.starred.athen.modules.Module
import foo.starred.snowbird.handlers.Observable

class ConfigMainBuilder(
    val configKey: String,
    name: String,
    description: String,
    category: Category,
    private val default: Boolean = false
) : ConfigScope {
    val feature = ConfigManager.feature(name, description, category, configKey, default)
    val state = Observable(default)

    override val builder: ConfigMainBuilder
        get() = this

    val value: Boolean
        get() = state.value

    var module: Module? = null
        internal set

    init {
        Athen.LOGGER.debug("Feature added for {}: {}", configKey, feature)

        ConfigManager.observe(configKey) {
            state.value = it as? Boolean ?: default
        }
    }

    fun observe(call: (Boolean) -> Unit): Observable<Boolean> {
        return state.onChange(call)
    }
}