package foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl

import net.minecraft.client.gui.GuiGraphics
//~ if >= 26.1 'gui.render.state.GuiElementRenderState' -> 'renderer.state.gui.GuiElementRenderState'
import net.minecraft.client.gui.render.state.GuiElementRenderState
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

open class RenderStatePrimitive : IPrimitiveElement<RenderStatePrimitive>() {
    override var x: Int = 0
    override var y: Int = 0
    override var width: Int = 0
    override var height: Int = 0
    override var color: Int = -1

    var state: GuiElementRenderState? = null
    var provider: ((GuiGraphics) -> GuiElementRenderState?)? = null
    var ascend: Boolean = false

    override fun render(graphics: GuiGraphics) {
        if (!visible) return

        val s = state ?: provider?.invoke(graphics)
        if (s != null) {
            //~ if >= 26.1 'submitGuiElement' -> 'addGuiElement'
            graphics.guiRenderState.submitGuiElement(s)
            if (ascend) graphics.guiRenderState.nextStratum()
        }

        super.render(graphics)
    }

    companion object {
        val NONE = RenderStatePrimitive()

        inline fun renderState(block: RenderStatePrimitive.() -> Unit): RenderStatePrimitive {
            return RenderStatePrimitive().apply(block)
        }
    }
}