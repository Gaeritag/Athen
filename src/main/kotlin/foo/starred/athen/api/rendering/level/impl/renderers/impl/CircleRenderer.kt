package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.athen.api.rendering.level.rendertypes.LevelRenderTypeImpl
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@LevelRenderer
object CircleRenderer : ILevelRenderer {
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        fn0(pose, consumers)
        fn1(pose, consumers)
    }

    fun fn0(pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.circles0) { depth, circles ->
            val type = if (depth) LevelRenderTypeImpl.LINES.depth else LevelRenderTypeImpl.LINES.depthless
            val buffer = consumers.getBuffer(type)

            for (circle in circles) {
                val pts = circle.center.points(circle.radius, circle.segments, circle.normal)

                for (i in 0 until circle.segments) {
                    pose.vertex(
                        buffer,
                        pts[i].x,
                        pts[i].y,
                        pts[i].z,
                        pts[i + 1].x,
                        pts[i + 1].y,
                        pts[i + 1].z,
                        circle.width,
                        circle.color
                    )
                }
            }
        }
    }

    fun fn1(pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        forDepth(LevelQueueImpl.circles1) { depth, circles ->
            val type = if (depth) LevelRenderTypeImpl.TRIANGLE_FAN.depth else LevelRenderTypeImpl.TRIANGLE_FAN.depthless

            for (circle in circles) {
                val buffer = consumers.getBuffer(type)
                val (u, v) = circle.normal.tangents()
                val center = circle.center

                val x0 = center.x.toFloat()
                val y0 = center.y.toFloat()
                val z0 = center.z.toFloat()
                buffer.addVertex(pose, x0, y0, z0).setColor(circle.color)

                val segments = circle.segments
                val radius = circle.radius

                for (i in 0..segments) {
                    val angle = 2.0 * Math.PI * i / segments
                    val cos = cos(angle)
                    val sin = sin(angle)

                    val x = x0 + radius * (u.x * cos + v.x * sin)
                    val y = y0 + radius * (u.y * cos + v.y * sin)
                    val z = z0 + radius * (u.z * cos + v.z * sin)

                    buffer.addVertex(pose, x.toFloat(), y.toFloat(), z.toFloat()).setColor(circle.color)
                }

                consumers.endBatch(type)
            }
        }
    }

    private fun Vec3.tangents(): Pair<Vec3, Vec3> {
        val n = normalize()
        val arbitrary = if (abs(n.x) < 0.9) Vec3(1.0, 0.0, 0.0) else Vec3(0.0, 1.0, 0.0)
        val u = n.cross(arbitrary).normalize()
        val v = n.cross(u).normalize()
        return u to v
    }

    private fun Vec3.points( radius: Double, segments: Int, normal: Vec3): Array<Vector3f> {
        val (u, v) = normal.tangents()
        return Array(segments + 1) { i ->
            val angle = 2.0 * Math.PI * i / segments
            val cos = cos(angle)
            val sin = sin(angle)
            Vector3f(
                (x + radius * (u.x * cos + v.x * sin)).toFloat(),
                (y + radius * (u.y * cos + v.y * sin)).toFloat(),
                (z + radius * (u.z * cos + v.z * sin)).toFloat()
            )
        }
    }
}