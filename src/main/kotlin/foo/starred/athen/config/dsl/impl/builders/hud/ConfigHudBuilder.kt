package foo.starred.athen.config.dsl.impl.builders.hud

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigHudElementData
import foo.starred.athen.config.dsl.base.ElementBuilder
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.athen.config.hud.data.element.HudElement
import foo.starred.athen.config.hud.data.element.HudElementCoordinateData
import foo.starred.athen.config.hud.impl.HudRenderer
import foo.starred.snowbird.api.data.Observable
import foo.starred.snowbird.utils.toCamelCase
import net.minecraft.client.gui.GuiGraphicsExtractor
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigHudBuilder(
    val builder: ConfigMainBuilder,
    val name: String,
    val default: Boolean = true,
    var outside: Boolean = true,
    parent: String? = null
) : ElementBuilder<ConfigHudBuilder>(parent), ReadOnlyProperty<Any?, ConfigHudBuilder> {
    private var constraint: (() -> Pair<Number, Number>)? = null
    private var bool = false

    val coordinate = HudElementCoordinateData()
    val state = Observable(default)

    var renderer: (GuiGraphicsExtractor.() -> Unit)? = null
    var preview: (GuiGraphicsExtractor.() -> Unit)? = null
    var center = emptyList<Int>()

    lateinit var key: String
        private set

    fun render(block: GuiGraphicsExtractor.() -> Unit) {
        renderer = block
    }

    fun preview(block: GuiGraphicsExtractor.() -> Unit) {
        preview = block
    }

    fun constrain(block: () -> Pair<Number, Number>) {
        constraint = block
    }

    fun constrain() {
        val (width, height) = constraint?.invoke() ?: return

        coordinate.width = width.toFloat()
        coordinate.height = height.toFloat()
    }

    fun unique(key: String): ConfigHudBuilder {
        this.key = "${builder.key}.$key"
        register()
        return this
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, ConfigHudBuilder> {
        if (!::key.isInitialized) key = "${builder.key}.${property.name}"
        register()
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): ConfigHudBuilder {
        return this
    }

    private fun register() {
        if (bool) return

        bool = true
        if (!::key.isInitialized) key = "${builder.key}.${name.toCamelCase()}"

        coordinate.x = (ConfigManager.get("$key.x") as? Number)?.toFloat() ?: 20f
        coordinate.y = (ConfigManager.get("$key.y") as? Number)?.toFloat() ?: 20f
        coordinate.scale = (ConfigManager.get("$key.scale") as? Number)?.toFloat() ?: 1f

        builder.feature.option(ConfigHudElementData(name, key, default, coordinate, this, builder, parent, description))
        HudRenderer.elements[key] = HudElement(coordinate, this)

        ConfigManager.observe(key) {
            state.value = it as? Boolean ?: default
        }
    }

    inline val GuiGraphicsExtractor.graphics: GuiGraphicsExtractor
        get() = this
}
