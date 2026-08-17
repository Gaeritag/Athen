package foo.starred.athen.config.dsl.impl.builders.hud

import foo.starred.athen.config.data.impl.ConfigHudElementData
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.athen.hud.HUDElement

class ConfigHudBuilder(
    val builder: ConfigMainBuilder,
    val element: HUDElement
) {
    val enabled by element::enabled
    var x by element::x
    var y by element::y
    var scale by element::scale

    fun description(description: String) = apply {
        val list = builder.feature.options
        val i = list.indexOfFirst { it is ConfigHudElementData && it.key == element.id }
        if (i >= 0) list[i] = (list[i] as ConfigHudElementData).copy(description = description)
    }
}