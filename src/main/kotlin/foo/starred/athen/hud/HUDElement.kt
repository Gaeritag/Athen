package foo.starred.athen.hud

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.snowbird.api.ZERO_PAIR
import net.minecraft.client.gui.GuiGraphicsExtractor

data class HUDElement(
    val id: String,
    val name: String,
    val config: ConfigMainBuilder,
    var renderer: GuiGraphicsExtractor.(Boolean) -> Pair<Int, Int>?,
    var defaultX: Float = 20f,
    var defaultY: Float = 20f,
    var defaultScale: Float = 1f,
    var enabled: Boolean = false,
    var renderOutsidePreview: Boolean = true
) {
    var width: Int = 1
        private set
    var height: Int = 1
        private set

    var x: Float = defaultX
    var y: Float = defaultY
    var scale: Float = defaultScale

    val render: Boolean
        get() = config.value && enabled

    val render0: Boolean
        get() = (config.module?.enabled ?: config.value) && enabled && renderOutsidePreview

    init {
        ConfigManager.observe(id) { enabled = it as? Boolean ?: false }
    }

    fun render(graphics: GuiGraphicsExtractor, isPreview: Boolean) {
        val (w, h) = graphics.renderer(isPreview) ?: ZERO_PAIR
        width = w
        height = h
    }

    fun isHovered(mx: Float, my: Float) =
        mx >= x - 4f * scale && mx <= x + (width + 4f) * scale && my >= y - 4f * scale && my <= y + (height + 4f) * scale
}
