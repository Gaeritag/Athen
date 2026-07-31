package foo.starred.athen.api.rendering.ui.shapes.rectangle

import net.minecraft.client.gui.GuiGraphics
import java.awt.Color

@JvmOverloads
@JvmName("drawRectangle_color")
fun GuiGraphics.rectangle(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
    fill(x, y, x + width, y + height, color.rgb)
}

@JvmOverloads
@JvmName("drawRectangle_int")
fun GuiGraphics.rectangle(x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    fill(x, y, x + width, y + height, color)
}