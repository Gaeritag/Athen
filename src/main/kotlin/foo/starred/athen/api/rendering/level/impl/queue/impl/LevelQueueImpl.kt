package foo.starred.athen.api.rendering.level.impl.queue.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.rendering.level.impl.data.impl.*
import foo.starred.athen.api.rendering.level.impl.queue.base.ILevelQueue
import foo.starred.athen.api.rendering.level.impl.queue.data.ExtractedQueue
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.client

@Load
object LevelQueueImpl : ILevelQueue {
    val renderers: MutableList<ILevelRenderer> = mutableListOf()

    override val beams: MutableList<ExtractedBeam> = mutableListOf()
    override val texts: MutableList<ExtractedText> = mutableListOf()
    override val lines: ExtractedQueue<ExtractedLine> = ExtractedQueue()
    override val boxes0: ExtractedQueue<ExtractedBox> = ExtractedQueue()
    override val boxes1: ExtractedQueue<ExtractedBox> = ExtractedQueue()
    override val circles0: ExtractedQueue<ExtractedCircle> = ExtractedQueue()
    override val circles1: ExtractedQueue<ExtractedCircle> = ExtractedQueue()

    init {
        on<WorldRenderEvent.Extract>(Int.MIN_VALUE) {
            clear()
        }

        on<WorldRenderEvent.Render> {
            //~ if >= 26.2 'mainCamera' -> 'mainCamera()'
            val camera = client.gameRenderer.mainCamera.position()

            pose.pushPose()
            pose.translate(-camera.x, -camera.y, -camera.z)

            for (r in renderers) r.render(pose, pose.last(), consumers)

            pose.popPose()
        }
    }

    override fun clear() {
        beams.clear()
        texts.clear()
        lines.clear()
        boxes0.clear()
        boxes1.clear()
        circles0.clear()
        circles1.clear()
    }
}