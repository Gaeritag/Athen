package foo.starred.athen.api.rendering.level.pipelines.base

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexFormat
import foo.starred.athen.Athen
import foo.starred.athen.api.rendering.level.pipelines.depth.LevelPipelineDepth
import kotlin.jvm.optionals.getOrNull

//? if >= 26.2
//import com.mojang.blaze3d.PrimitiveTopology

class LevelPipelineBuilder {
    lateinit var snippet: RenderPipeline.Snippet
    lateinit var location: String

    var depth: LevelPipelineDepth = LevelPipelineDepth.LEQUAL
    var cull: Boolean = true

    var vertexFormat: VertexFormat? = null
    //~ if >= 26.2 'VertexFormat.Mode' -> 'PrimitiveTopology'
    var vertexMode: VertexFormat.Mode? = null

    fun build(): RenderPipeline {
        //~ if >= 26.2 'snippet.vertexFormat.getOrNull()' -> 'snippet.vertexFormatPerBuffer()?.firstOrNull()'
        if (vertexFormat == null) vertexFormat = snippet.vertexFormat.getOrNull()
        //~ if >= 26.2 'vertexFormatMode' -> 'vertexFormatMode()'
        if (vertexMode == null) vertexMode = snippet.vertexFormatMode.getOrNull()

        val a = RenderPipeline.builder(snippet).withLocation("${Athen.modId}/$location")
        val b = vertexFormat
        if (b != null) {
            //? if >= 26.2 {
            /*a.withVertexBinding(0, b)
            a.withPrimitiveTopology(vertexMode ?: PrimitiveTopology.QUADS)
            *///? } else
            a.withVertexFormat(b, vertexMode ?: VertexFormat.Mode.QUADS)
        }

        depth.build(a)

        if (!cull) a.withCull(false)
        return a.build()
    }
}