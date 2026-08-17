package foo.starred.athen.config.data.impl

import foo.starred.athen.config.data.base.IConfigElementData

data class ConfigTextInputElementData(
    override val name: String,
    override val key: String,
    val default: String,
    val placeholder: String,
    val max: Int,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigTextInputElementData {
        return copy(key = key, parent = parent, description = description)
    }
}