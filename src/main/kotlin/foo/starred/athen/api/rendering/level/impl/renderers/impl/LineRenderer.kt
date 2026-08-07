package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.athen.api.rendering.level.rendertypes.LevelRenderTypeImpl
import net.minecraft.client.renderer.MultiBufferSource

@LevelRenderer
object LineRenderer : ILevelRenderer {
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        fn(pose, consumers)
    }

    fun fn(pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.lines) { depth, lines ->
            val buffer = consumers.getBuffer(if (depth) LevelRenderTypeImpl.LINES.depth else LevelRenderTypeImpl.LINES.depthless)

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
}