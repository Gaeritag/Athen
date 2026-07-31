package foo.starred.athen.api.rendering.level.impl.data.impl

import org.joml.Vector3f
import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable

data class ExtractedLine(
    val start: Vector3f,
    val end: Vector3f,
    val color: Int,
    val width: Float
) : ILevelExtractable