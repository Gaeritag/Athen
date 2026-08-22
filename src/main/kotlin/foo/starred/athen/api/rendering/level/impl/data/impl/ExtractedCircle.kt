package foo.starred.athen.api.rendering.level.impl.data.impl

import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable
import net.minecraft.world.phys.Vec3

data class ExtractedCircle(
    val center: Vec3,
    val radius: Double,
    val segments: Int,
    val color: Int,
    val width: Float,
    val normal: Vec3
) : ILevelExtractable