package foo.starred.athen.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

fun ItemStack.customData(): CompoundTag? {
    return get(DataComponents.CUSTOM_DATA)?.copyTag()
}

fun ItemStack.glint(): Boolean {
    return components.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) != null
}

fun ItemStack.id(): String? {
    return customData()?.getString("id")?.getOrNull()
}

fun ItemStack.uuid(): String? {
    return customData()?.getString("uuid")?.getOrNull()
}

fun ItemStack.timestamp(): Long? {
    return customData()?.getLong("timestamp")?.getOrNull()
}

fun ItemStack.stars(): Int? {
    return customData()?.getInt("upgrade_level")?.getOrNull() ?: customData()?.getInt("dungeon_item_level")?.getOrNull()
}

fun ItemStack.quality(): Int? {
    return customData()?.getInt("baseStatBoostPercentage")?.getOrNull()
}

fun ItemStack.tier(): Int? {
    return customData()?.getInt("dungeon_tier")?.getOrNull() ?: customData()?.getInt("item_tier")?.getOrNull()
}
