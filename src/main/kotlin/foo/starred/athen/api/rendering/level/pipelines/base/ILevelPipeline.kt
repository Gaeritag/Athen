package foo.starred.athen.api.rendering.level.pipelines.base

import com.mojang.blaze3d.pipeline.RenderPipeline

interface ILevelPipeline {
    val depth: RenderPipeline
    val depthless: RenderPipeline
}