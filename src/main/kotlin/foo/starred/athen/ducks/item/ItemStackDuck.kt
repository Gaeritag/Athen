@file:Suppress("FunctionName", "ObjectPropertyName", "Cast_Never_Succeeds", "Unused")

package foo.starred.athen.ducks.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

@JvmSuppressWildcards
interface ItemStackDuck {
    fun `athen$cache$tooltip`(): Pair<List<Component>, List<Component>>?
    fun `athen$cache$tooltip`(pair: Pair<List<Component>, List<Component>>?)

    companion object {
        var ItemStack.`athen$cached$tooltip`
            get() = (this as ItemStackDuck).`athen$cache$tooltip`()
            set(value) = (this as ItemStackDuck).`athen$cache$tooltip`(value)
    }
}