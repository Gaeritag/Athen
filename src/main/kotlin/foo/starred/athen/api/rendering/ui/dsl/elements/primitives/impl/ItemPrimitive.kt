package foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

open class ItemPrimitive : IPrimitiveElement<ItemPrimitive>() {
    override var x: Int = 0
    override var y: Int = 0
    override var width: Int = 16
    override var height: Int = 16
    override var color: Int = -1

    override var interact: Boolean = false

    var item: ItemStack = ItemStack.EMPTY

    override fun render(graphics: GuiGraphics) {
        if (!visible) return
        if (item.isEmpty) return

        //~ if >= 26.1 'renderItem(' -> 'item('
        graphics.renderItem(item, x, y)
        super.render(graphics)
    }

    companion object {
        val NONE = ItemPrimitive()

        inline fun item(block: ItemPrimitive.() -> Unit): ItemPrimitive {
            return ItemPrimitive().apply(block)
        }
    }
}