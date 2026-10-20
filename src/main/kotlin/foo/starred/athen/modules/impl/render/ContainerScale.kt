package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.gui.MinecraftGui
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen

@LoadableAthenModule
object ContainerScale : Module(
    "Container scale",
    "Scales containers visually",
    ConfigCategory.RENDER
) {
    private val inventory by config.switch("Inventory only")
    val scale by config.slider("Scale", 1f, 0.5f, 5f, double = true)

    @JvmStatic
    val x0: Float
        get() = (MinecraftGui.screen?.width ?: 0) / 2f

    @JvmStatic
    val y0: Float
        get() = (MinecraftGui.screen?.height ?: 0) / 2f

    @JvmStatic
    val bool: Boolean
        get() {
            if (!enabled) return false
            val screen = MinecraftGui.screen ?: return false
            return if (inventory) screen is InventoryScreen else screen is AbstractContainerScreen<*>
        }

    init {
        on<GuiEvent.Render.Screen.Pre>(Int.MIN_VALUE) {
            val screen = MinecraftGui.container ?: return@on
            if (!bool) return@on

            graphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680)
            graphics.pose().pushMatrix()
            graphics.pose().translate(x0, y0)
            graphics.pose().scale(scale)
            graphics.pose().translate(-x0, -y0)
        }

        on<GuiEvent.Render.Screen.Post>(Int.MIN_VALUE) {
            if (!bool) return@on

            graphics.pose().popMatrix()
        }
    }
}
