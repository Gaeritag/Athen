package foo.starred.athen.api.rendering.level.rendertypes.impl

import foo.starred.athen.api.rendering.level.pipelines.base.ILevelPipeline
import foo.starred.athen.api.rendering.level.rendertypes.base.ILevelRenderType
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

class DualLevelRenderType(identifier: String, pipeline: ILevelPipeline) : ILevelRenderType {
    override val depth: RenderType =
        RenderType.create(
            "starred_$identifier",
            RenderSetup.builder(pipeline.depth)
                .bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
                .createRenderSetup()
        )

    override val depthless: RenderType =
        RenderType.create(
            "starred_${identifier}_depthless",
            RenderSetup.builder(pipeline.depthless)
                .bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
                .createRenderSetup()
        )
}