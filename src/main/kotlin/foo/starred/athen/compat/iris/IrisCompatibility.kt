package foo.starred.athen.compat.iris

import foo.starred.athen.Athen
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.rendering.level.pipelines.LevelPipelineImpl
import net.fabricmc.loader.api.FabricLoader
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram

@Load
object IrisCompatibility {
    init {
        if (FabricLoader.getInstance().isModLoaded("iris")) fn()
    }

    fun fn() {
        Athen.LOGGER.info("Attempting to ensure Iris compatibility for rendering...")

        try {
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.LINES.depth, IrisProgram.LINES)
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.LINES.depthless, IrisProgram.LINES)
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.DEBUG_FILLED.depth, IrisProgram.BASIC)
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.DEBUG_FILLED.depthless, IrisProgram.BASIC)
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.TRIANGLE_FAN.depth, IrisProgram.BASIC)
            IrisApi.getInstance().assignPipeline(LevelPipelineImpl.TRIANGLE_FAN.depthless, IrisProgram.BASIC)

            Athen.LOGGER.info("Registered pipelines to Iris API!")
        } catch (e: Exception) {
            Athen.LOGGER.error("Failed to try to ensure Iris compatibility, issues may occur!", e)
        }
    }
}