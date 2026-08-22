package foo.starred.athen.api.rendering.level.impl.data.impl

import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable
import net.minecraft.world.phys.AABB

data class ExtractedBox(
    val aabb: AABB,
    val color: Int,
    val width: Float
) : ILevelExtractable