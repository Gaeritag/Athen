@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.extensions.impl

import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedCircle
import foo.starred.athen.api.rendering.level.impl.extensions.base.RenderableStyle
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import net.minecraft.world.phys.Vec3

val CIRCLE_NORMAL = Vec3(0.0, 1.0, 0.0)

@JvmOverloads
fun extractFrameCircle(
    center: Vec3,
    radius: Double,
    color: Int,
    segments: Int = 64,
    normal: Vec3 = CIRCLE_NORMAL,
    width: Float = 2f,
    depth: Boolean = true
) {
    LevelQueueImpl.circles0.add(ExtractedCircle(center, radius, segments, color, width, normal), depth)
}

@JvmOverloads
fun extractFilledCircle(
    center: Vec3,
    radius: Double,
    color: Int,
    segments: Int = 64,
    normal: Vec3 = CIRCLE_NORMAL,
    depth: Boolean = true
) {
    LevelQueueImpl.circles1.add(ExtractedCircle(center, radius, segments, color, 1f, normal), depth)
}

@JvmOverloads
fun extractStyledCircle(
    center: Vec3,
    radius: Double,
    color: Int,
    style: Int = RenderableStyle.BOTH.id,
    segments: Int = 64,
    normal: Vec3 = CIRCLE_NORMAL,
    width: Float = 2f,
    depth: Boolean = true
) {
    when (style) {
        RenderableStyle.OUTLINE.id -> {
            extractFrameCircle(center, radius, color, segments, normal, width, depth)
        }

        RenderableStyle.FILLED.id -> {
            extractFilledCircle(center, radius, color, segments, normal, depth)
        }

        RenderableStyle.BOTH.id -> {
            extractFilledCircle(center, radius, color and 0x00FFFFFF or ((color ushr 25) shl 24), segments, normal, depth)
            extractFrameCircle(center, radius, color, segments, normal, width, depth)
        }
    }
}