package foo.starred.athen.config.dsl.impl.builders.config

import foo.starred.athen.Athen
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.dsl.base.ConfigScope
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.modules.Module
import foo.starred.kontext.api.data.Observable

class ConfigMainBuilder(
    val key: String,
    name: String,
    description: String,
    category: ConfigCategory,
    val default: Boolean = false,
) : ConfigScope {
    val feature = ConfigManager.feature(name, description, category, key, default)
    val state = Observable(default)

    override val builder: ConfigMainBuilder
        get() = this

    val value: Boolean
        get() = state.value

    var module: Module? = null
        internal set

    init {
        Athen.LOGGER.debug("Feature added for {}: {}", key, feature)

        ConfigManager.observe(key) {
            state.value = it as? Boolean ?: default
        }
    }
}
