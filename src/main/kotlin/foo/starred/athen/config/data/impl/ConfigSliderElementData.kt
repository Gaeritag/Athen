package foo.starred.athen.config.data.impl

import foo.starred.athen.config.data.base.IConfigElementData

data class ConfigSliderElementData(
    override val name: String,
    override val key: String,
    val min: Double,
    val max: Double,
    val default: Double,
    val double: Boolean,
    val unit: String,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigSliderElementData {
        return copy(key = key, parent = parent, description = description)
    }
}