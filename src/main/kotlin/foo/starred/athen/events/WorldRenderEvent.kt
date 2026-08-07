package foo.starred.athen.events

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.athen.events.core.CancellableEvent
import foo.starred.athen.events.core.Event
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.state.EntityRenderState
//~ if >= 26.1 'CameraRenderState' -> 'level.CameraRenderState'
import net.minecraft.client.renderer.state.CameraRenderState

sealed class WorldRenderEvent {
    sealed class Entity {
        data class Pre(
            val renderState: EntityRenderState,
            val poseStack: PoseStack,
            val cameraRenderState: CameraRenderState,
            val entity: net.minecraft.world.entity.Entity?
        ) : CancellableEvent()

        data class Post(
            val renderState: EntityRenderState,
            val poseStack: PoseStack,
            val cameraRenderState: CameraRenderState,
            val entity: net.minecraft.world.entity.Entity?
        ) : Event()
    }

    data object Extract : Event()

    data class Render(val pose: PoseStack, val consumers: MultiBufferSource.BufferSource) : Event()
}
