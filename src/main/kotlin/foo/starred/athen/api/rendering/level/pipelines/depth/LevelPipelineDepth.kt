@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.pipelines.depth

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.platform.CompareOp
import java.util.Optional

enum class LevelPipelineDepth(val vanilla: Optional<DepthStencilState>) {
    NONE(Optional.empty()),
    EQUAL(Optional.of(DepthStencilState(CompareOp.EQUAL, true))),
    LEQUAL(Optional.of(DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))),
    LESS(Optional.of(DepthStencilState(CompareOp.LESS_THAN, true))),
    GREATER(Optional.of(DepthStencilState(CompareOp.GREATER_THAN, true)));

    fun build(builder: RenderPipeline.Builder) {
        builder.withDepthStencilState(vanilla)
    }
}