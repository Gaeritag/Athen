@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.extensions.impl

import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedText
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.parser.parse
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3

@JvmOverloads
fun extractText(
    text: String,
    pos: Vec3,
    color0: Int = -1,
    color1: Int = 0,
    scale: Float = 1f,
    depth: Boolean = true,
    shadow: Boolean = true,
    increase: Boolean = false
) {
    if (!increase) {
        LevelQueueImpl.texts.add(ExtractedText(text.parse(), pos, color0, color1, scale, shadow, depth))
        return
    }

    val scale = scale * client.gameRenderer.mainCamera.position().distanceTo(pos).toFloat() / 3f
    LevelQueueImpl.texts.add(ExtractedText(text.parse(), pos, color0, color1, scale, shadow, depth))
}

@JvmOverloads
fun extractText(
    text: Component,
    pos: Vec3,
    color0: Int = -1,
    color1: Int = 0,
    scale: Float = 1f,
    depth: Boolean = true,
    shadow: Boolean = true,
    increase: Boolean = false
) {
    if (!increase) {
        LevelQueueImpl.texts.add(ExtractedText(text, pos, color0, color1, scale, shadow, depth))
        return
    }

    val scale = scale * client.gameRenderer.mainCamera.position().distanceTo(pos).toFloat() / 3f
    LevelQueueImpl.texts.add(ExtractedText(text, pos, color0, color1, scale, shadow, depth))
}