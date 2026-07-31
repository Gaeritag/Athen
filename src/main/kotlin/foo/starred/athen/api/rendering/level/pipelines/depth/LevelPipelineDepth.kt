@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.pipelines.depth

import com.mojang.blaze3d.pipeline.RenderPipeline
//? if >= 26.1 {
/*import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.platform.CompareOp
import java.util.Optional
*///? } else {
import com.mojang.blaze3d.platform.DepthTestFunction
//? }

//~ if >= 26.1 'DepthTestFunction' -> 'Optional<DepthStencilState>'
enum class LevelPipelineDepth(val vanilla: DepthTestFunction) {
    //~ if >= 26.1 'DepthTestFunction.NO_DEPTH_TEST' -> 'Optional.empty()'
    NONE(DepthTestFunction.NO_DEPTH_TEST),
    //~ if >= 26.1 'DepthTestFunction.EQUAL_DEPTH_TEST' -> 'Optional.of(DepthStencilState(CompareOp.EQUAL, true))'
    EQUAL(DepthTestFunction.EQUAL_DEPTH_TEST),
    //~ if >= 26.1 'DepthTestFunction.LEQUAL_DEPTH_TEST' -> 'Optional.of(DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))'
    LEQUAL(DepthTestFunction.LEQUAL_DEPTH_TEST),
    //~ if >= 26.1 'DepthTestFunction.LESS_DEPTH_TEST' -> 'Optional.of(DepthStencilState(CompareOp.LESS_THAN, true))'
    LESS(DepthTestFunction.LESS_DEPTH_TEST),
    //~ if >= 26.1 'DepthTestFunction.GREATER_DEPTH_TEST' -> 'Optional.of(DepthStencilState(CompareOp.GREATER_THAN, true))'
    GREATER(DepthTestFunction.GREATER_DEPTH_TEST);

    fun build(builder: RenderPipeline.Builder) {
        //~ if >= 26.1 'withDepthTestFunction' -> 'withDepthStencilState'
        builder.withDepthTestFunction(vanilla)
    }
}