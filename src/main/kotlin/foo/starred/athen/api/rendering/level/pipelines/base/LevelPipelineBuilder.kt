package foo.starred.athen.api.rendering.level.pipelines.base

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexFormat
import foo.starred.athen.Athen
import foo.starred.athen.api.rendering.level.pipelines.depth.LevelPipelineDepth
import kotlin.jvm.optionals.getOrNull

class LevelPipelineBuilder {
    lateinit var snippet: RenderPipeline.Snippet
    lateinit var location: String

    var depth: LevelPipelineDepth = LevelPipelineDepth.LEQUAL
    var cull: Boolean = true

    var vertexFormat: VertexFormat? = null
    var vertexMode: VertexFormat.Mode? = null

    fun build(): RenderPipeline {
        if (vertexFormat == null) vertexFormat = snippet.vertexFormat.getOrNull()
        if (vertexMode == null) vertexMode = snippet.vertexFormatMode.getOrNull()

        val a = RenderPipeline.builder(snippet).withLocation("${Athen.modId}/$location")
        val b = vertexFormat
        if (b != null) a.withVertexFormat(b, vertexMode ?: VertexFormat.Mode.QUADS)

        depth.build(a)

        if (!cull) a.withCull(false)
        return a.build()
    }
}