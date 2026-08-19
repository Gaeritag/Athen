@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.Font
//~ if >= 26.2 'MultiBufferSource' -> 'SubmitNodeCollector'
import net.minecraft.client.renderer.MultiBufferSource
//? if >= 26.2
//import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil

@LevelRenderer
object TextRenderer : ILevelRenderer {
    //~ if >= 26.2 'MultiBufferSource.BufferSource' -> 'SubmitNodeCollector'
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        if (LevelQueueImpl.texts.isEmpty()) return

        //~ if >= 26.2 'mainCamera' -> 'mainCamera()'
        val a = client.gameRenderer.mainCamera.rotation()

        for (text in LevelQueueImpl.texts) {
            poseStack.pushPose()

            val scale = text.scale * 0.025f
            poseStack.translate(text.pos.x, text.pos.y, text.pos.z)
            poseStack.mulPose(a)
            poseStack.scale(scale, -scale, scale)

            val x = -client.font.width(text.text) / 2f
            val mode = if (text.depth) Font.DisplayMode.NORMAL else Font.DisplayMode.SEE_THROUGH

            //? if >= 26.2 {
            //consumers.submitText(poseStack, x, 0f, text.text.visualOrderText, text.shadow, mode, text.color0, text.color1, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY)
            //? } else {
            client.font.drawInBatch(text.text, x, 0f, text.color0, text.shadow, poseStack.last().pose(), consumers, mode, text.color1, LightCoordsUtil.FULL_BRIGHT)
            //? }

            poseStack.popPose()
        }
    }
}