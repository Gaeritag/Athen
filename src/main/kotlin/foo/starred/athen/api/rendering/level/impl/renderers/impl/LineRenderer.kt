@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedLine
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.athen.api.rendering.level.rendertypes.LevelRenderTypeImpl
//~ if >= 26.2 'MultiBufferSource' -> 'SubmitNodeCollector'
import net.minecraft.client.renderer.MultiBufferSource

@LevelRenderer
object LineRenderer : ILevelRenderer {
    //~ if >= 26.2 'MultiBufferSource.BufferSource' -> 'SubmitNodeCollector'
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.lines) { depth, lines ->
            val type = if (depth) LevelRenderTypeImpl.LINES.depth else LevelRenderTypeImpl.LINES.depthless
            //? if >= 26.2 {
            /*consumers.submitCustomGeometry(poseStack, type) { pose, buffer -> fn(pose, buffer, lines) }
            *///? } else {
            fn(pose, consumers.getBuffer(type), lines)
            //? }
        }
    }

    private fun fn(pose: PoseStack.Pose, buffer: VertexConsumer, lines: List<ExtractedLine>) {
        for (line in lines) {
            pose.vertex(
                buffer,
                line.start.x,
                line.start.y,
                line.start.z,
                line.end.x,
                line.end.y,
                line.end.z,
                line.width,
                line.color
            )
        }
    }
}