package foo.starred.athen.config.data.impl

import foo.starred.athen.config.data.base.IConfigElementData

data class ConfigVariablesElementData(
    override val name: String = "Variables",
    override val key: String,
    val tokens: List<String>,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigVariablesElementData {
        return copy(key = key, parent = parent, description = description)
    }
}