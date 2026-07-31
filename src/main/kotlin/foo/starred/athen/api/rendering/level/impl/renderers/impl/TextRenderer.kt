package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.snowbird.api.client

//? if >= 26.1 {
/*import net.minecraft.util.LightCoordsUtil
*///? } else {
import net.minecraft.client.renderer.LightTexture
//? }

@LevelRenderer
object TextRenderer : ILevelRenderer {
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        fn(poseStack, consumers)
    }

    private fun fn(poseStack: PoseStack, consumers: MultiBufferSource.BufferSource) {
        val a = client.gameRenderer.mainCamera.rotation()

        for (text in LevelQueueImpl.texts) {
            poseStack.pushPose()

            val scale = text.scale * 0.025f
            poseStack.translate(text.pos.x, text.pos.y, text.pos.z)
            poseStack.mulPose(a)
            poseStack.scale(scale, -scale, scale)

            client.font.drawInBatch(
                text.text,
                -client.font.width(text.text) / 2f,
                0f,
                text.color0,
                text.shadow,
                poseStack.last().pose(),
                consumers,
                if (text.depth) Font.DisplayMode.NORMAL else Font.DisplayMode.SEE_THROUGH,
                text.color1,
                //~ if >= 26.1 'LightTexture' -> 'LightCoordsUtil'
                LightTexture.FULL_BRIGHT
            )

            poseStack.popPose()
        }
    }
}