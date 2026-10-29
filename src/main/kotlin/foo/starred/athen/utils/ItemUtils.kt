package foo.starred.athen.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

fun ItemStack.glint(): Boolean {
    return components.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) != null
}
