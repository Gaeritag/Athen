package foo.starred.athen.api.rendering.level.pipelines

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.rendering.level.pipelines.impl.DualLevelPipeline

@Load
object LevelPipelineImpl {
    val LINES = DualLevelPipeline("line") {
        snippet = RenderPipelines.LINES_SNIPPET
    }

    val DEBUG_FILLED = DualLevelPipeline("debug_filled") {
        snippet = RenderPipelines.DEBUG_FILLED_SNIPPET
    }

    val TRIANGLE_FAN = DualLevelPipeline("triangle_fan") {
        snippet = RenderPipelines.DEBUG_FILLED_SNIPPET
        vertexMode = VertexFormat.Mode.TRIANGLE_FAN
        cull = false
    }
}