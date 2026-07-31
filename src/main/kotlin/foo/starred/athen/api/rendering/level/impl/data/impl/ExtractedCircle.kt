package foo.starred.athen.api.rendering.level.impl.data.impl

import net.minecraft.world.phys.Vec3
import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable

data class ExtractedCircle(
    val center: Vec3,
    val radius: Double,
    val segments: Int,
    val color: Int,
    val width: Float,
    val normal: Vec3
) : ILevelExtractable