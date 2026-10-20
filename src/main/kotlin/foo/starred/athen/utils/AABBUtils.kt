package foo.starred.athen.utils

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB

fun BlockPos.aabb(): AABB {
    return AABB(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
}
