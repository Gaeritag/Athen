package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.athen.api.rendering.level.rendertypes.LevelRenderTypeImpl
import net.minecraft.client.renderer.MultiBufferSource

@LevelRenderer
object BoxRenderer : ILevelRenderer {
    private val edges = arrayOf(
        intArrayOf(0, 0, 0, 1, 0, 0), intArrayOf(1, 0, 0, 1, 0, 1),
        intArrayOf(1, 0, 1, 0, 0, 1), intArrayOf(0, 0, 1, 0, 0, 0),
        intArrayOf(0, 1, 0, 1, 1, 0), intArrayOf(1, 1, 0, 1, 1, 1),
        intArrayOf(1, 1, 1, 0, 1, 1), intArrayOf(0, 1, 1, 0, 1, 0),
        intArrayOf(0, 0, 0, 0, 1, 0), intArrayOf(1, 0, 0, 1, 1, 0),
        intArrayOf(1, 0, 1, 1, 1, 1), intArrayOf(0, 0, 1, 0, 1, 1)
    )

    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        fn0(pose, consumers)
        fn1(pose, consumers)
    }

    private fun fn0(pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.boxes0) { depth, boxes ->
            val renderType = if (depth) LevelRenderTypeImpl.LINES.depth else LevelRenderTypeImpl.LINES.depthless
            val buffer = consumers.getBuffer(renderType)

            for (box in boxes) {
                val aabb = box.aabb
                val x0 = aabb.minX.toFloat()
                val x1 = aabb.maxX.toFloat()
                val y0 = aabb.minY.toFloat()
                val y1 = aabb.maxY.toFloat()
                val z0 = aabb.minZ.toFloat()
                val z1 = aabb.maxZ.toFloat()

                for (edge in edges) {
                    pose.vertex(
                        buffer,
                        if (edge[0] == 0) x0 else x1,
                        if (edge[1] == 0) y0 else y1,
                        if (edge[2] == 0) z0 else z1,
                        if (edge[3] == 0) x0 else x1,
                        if (edge[4] == 0) y0 else y1,
                        if (edge[5] == 0) z0 else z1,
                        box.width,
                        box.color
                    )
                }
            }
        }
    }

    private fun fn1(pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.boxes1) { depth, boxes ->
            val type = if (depth) LevelRenderTypeImpl.DEBUG_FILLED.depth else LevelRenderTypeImpl.DEBUG_FILLED.depthless
            val buffer = consumers.getBuffer(type)

            fun buff(x: Float, y: Float, z: Float, color: Int) {
                buffer.addVertex(pose, x, y, z).setColor(color)
            }

            for (box in boxes) {
                val aabb = box.aabb
                val color = box.color

                val x1 = aabb.minX.toFloat()
                val x2 = aabb.maxX.toFloat()
                val y1 = aabb.minY.toFloat()
                val y2 = aabb.maxY.toFloat()
                val z1 = aabb.minZ.toFloat()
                val z2 = aabb.maxZ.toFloat()

                buff(x1, y1, z1, color)
                buff(x1, y1, z2, color)
                buff(x1, y2, z2, color)
                buff(x1, y2, z1, color)

                buff(x2, y1, z2, color)
                buff(x2, y1, z1, color)
                buff(x2, y2, z1, color)
                buff(x2, y2, z2, color)

                buff(x1, y1, z1, color)
                buff(x1, y2, z1, color)
                buff(x2, y2, z1, color)
                buff(x2, y1, z1, color)

                buff(x2, y1, z2, color)
                buff(x2, y2, z2, color)
                buff(x1, y2, z2, color)
                buff(x1, y1, z2, color)

                buff(x1, y1, z1, color)
                buff(x2, y1, z1, color)
                buff(x2, y1, z2, color)
                buff(x1, y1, z2, color)

                buff(x1, y2, z2, color)
                buff(x2, y2, z2, color)
                buff(x2, y2, z1, color)
                buff(x1, y2, z1, color)
            }
        }
    }
}