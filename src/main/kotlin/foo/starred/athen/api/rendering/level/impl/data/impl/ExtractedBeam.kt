package foo.starred.athen.api.rendering.level.impl.data.impl

import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable
import net.minecraft.core.BlockPos

data class ExtractedBeam(
    val pos: BlockPos,
    val color: Int
) : ILevelExtractable