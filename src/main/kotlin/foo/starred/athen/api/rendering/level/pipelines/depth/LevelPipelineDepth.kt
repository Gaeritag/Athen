@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.pipelines.depth

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.platform.CompareOp
import java.util.Optional

enum class LevelPipelineDepth(val vanilla: Optional<DepthStencilState>) {
    NONE(Optional.empty()),
    EQUAL(Optional.of(DepthStencilState(CompareOp.EQUAL, true))),
    //~ if >= 26.2 'LESS_THAN_OR_EQUAL' -> 'GREATER_THAN_OR_EQUAL'
    LEQUAL(Optional.of(DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))),
    //~ if >= 26.2 'CompareOp.LESS_THAN,' -> 'CompareOp.GREATER_THAN,'
    LESS(Optional.of(DepthStencilState(CompareOp.LESS_THAN, true))),
    //~ if >= 26.2 'CompareOp.GREATER_THAN,' -> 'CompareOp.LESS_THAN,'
    GREATER(Optional.of(DepthStencilState(CompareOp.GREATER_THAN, true)));

    fun build(builder: RenderPipeline.Builder) {
        builder.withDepthStencilState(vanilla)
    }
}