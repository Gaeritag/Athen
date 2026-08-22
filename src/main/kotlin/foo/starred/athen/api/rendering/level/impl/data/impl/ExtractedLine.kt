package foo.starred.athen.api.rendering.level.impl.data.impl

import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable
import org.joml.Vector3f

data class ExtractedLine(
    val start: Vector3f,
    val end: Vector3f,
    val color: Int,
    val width: Float
) : ILevelExtractable