package foo.starred.athen.api.rendering.level.pipelines

//~ if >= 26.2 'import com.mojang.blaze3d.vertex.VertexFormat' -> 'import com.mojang.blaze3d.PrimitiveTopology'
import com.mojang.blaze3d.vertex.VertexFormat
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.rendering.level.pipelines.impl.DualLevelPipeline
import net.minecraft.client.renderer.RenderPipelines

@Load
object LevelPipelineImpl {
    val LINES = DualLevelPipeline("line") {
        snippet = RenderPipelines.LINES_SNIPPET
    }

    val DEBUG_FILLED = DualLevelPipeline("debug_filled") {
        snippet = RenderPipelines.DEBUG_FILLED_SNIPPET
        cull = false
    }

    val TRIANGLE_FAN = DualLevelPipeline("triangle_fan") {
        snippet = RenderPipelines.DEBUG_FILLED_SNIPPET
        //~ if >= 26.2 'VertexFormat.Mode.TRIANGLE_FAN' -> 'PrimitiveTopology.TRIANGLE_FAN'
        vertexMode = VertexFormat.Mode.TRIANGLE_FAN
        cull = false
    }
}