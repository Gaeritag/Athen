@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.extensions.impl

import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedLine
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import org.joml.Vector3f

@JvmOverloads
fun extractLine(
    v1: Vector3f,
    v2: Vector3f,
    color: Int,
    width: Float = 2f,
    depth: Boolean = true
) {
    LevelQueueImpl.lines.add(ExtractedLine(v1, v2, color, width), depth)
}

@JvmOverloads
fun extractLines(
    points: List<Vector3f>,
    color: Int,
    width: Float = 2f,
    depth: Boolean = true
) {
    if (points.size < 2) return

    for (i in 0 until points.size - 1) {
        LevelQueueImpl.lines.add(ExtractedLine(points[i], points[i + 1], color, width), depth)
    }
}