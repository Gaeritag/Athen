@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.extensions.impl

import net.minecraft.world.phys.AABB
import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedBox
import foo.starred.athen.api.rendering.level.impl.extensions.base.RenderableStyle
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl

@JvmOverloads
fun extractFrameBox(
    aabb: AABB,
    color: Int,
    width: Float = 2f,
    depth: Boolean = true
) {
    LevelQueueImpl.boxes0.add(ExtractedBox(aabb, color, width), depth)
}

@JvmOverloads
fun extractFilledBox(
    aabb: AABB,
    color: Int,
    depth: Boolean = true
) {
    LevelQueueImpl.boxes1.add(ExtractedBox(aabb, color, 1f), depth)
}

@JvmOverloads
fun extractStyledBox(
    aabb: AABB,
    color: Int,
    style: Int = RenderableStyle.BOTH.id,
    width: Float = 2f,
    depth: Boolean = true
) {
    when (style) {
        RenderableStyle.OUTLINE.id -> {
            extractFrameBox(aabb, color, width, depth)
        }

        RenderableStyle.FILLED.id -> {
            extractFilledBox(aabb, color, depth)
        }

        RenderableStyle.BOTH.id -> {
            extractFilledBox(aabb, color and 0x00FFFFFF or ((color ushr 25) shl 24), depth)
            extractFrameBox(aabb, color, width, depth)
        }
    }
}