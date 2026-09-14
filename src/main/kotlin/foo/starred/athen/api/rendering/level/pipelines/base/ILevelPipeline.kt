package foo.starred.athen.api.rendering.level.pipelines.base

//~ if >= 26.3 'blaze3d' -> 'renderpearl.api'
import com.mojang.blaze3d.pipeline.RenderPipeline

interface ILevelPipeline {
    val depth: RenderPipeline
    val depthless: RenderPipeline
}