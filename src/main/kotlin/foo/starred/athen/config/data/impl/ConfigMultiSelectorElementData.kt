package foo.starred.athen.config.data.impl

import foo.starred.athen.config.data.base.IConfigElementData

data class ConfigMultiSelectorElementData(
    override val name: String,
    override val key: String,
    val options: List<String>,
    val default: List<Int>,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigMultiSelectorElementData {
        return copy(key = key, parent = parent, description = description)
    }
}