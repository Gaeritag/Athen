@file:Suppress("ConstPropertyName")

package foo.starred.athen.hud

import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.mouseSX
import foo.starred.snowbird.utils.mouseSY
import net.minecraft.client.gui.GuiGraphicsExtractor

// Defaults to 1080 / 2, intended :eyes:
object Resolute {
    const val height = 540f

    var scale: Float = 1f
        private set

    var width: Float = 960f
        private set

    val mx: Float
        get() = s(mouseSX)

    val my: Float
        get() = s(mouseSY)

    fun s(f: Float): Float {
        return f / scale
    }

    fun push(graphics: GuiGraphicsExtractor) {
        graphics.pose().pushMatrix()
        graphics.pose().scale(scale, scale)
    }

    fun pop(graphics: GuiGraphicsExtractor) {
        graphics.pose().popMatrix()
    }

    /**
     * @see foo.starred.athen.mixin.mixins.WindowMixin
     */
    @JvmStatic
    fun update() {
        scale = client.window.guiScaledHeight.toFloat() / height
        width = client.window.guiScaledWidth.toFloat() / scale
    }
}