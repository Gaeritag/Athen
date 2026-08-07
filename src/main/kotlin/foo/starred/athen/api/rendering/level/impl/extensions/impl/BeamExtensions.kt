@file:Suppress("Unused")

package foo.starred.athen.api.rendering.level.impl.extensions.impl

import foo.starred.athen.api.rendering.level.impl.data.impl.ExtractedBeam
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import net.minecraft.core.BlockPos

fun extractBeam(
    pos: BlockPos,
    color: Int
) {
    LevelQueueImpl.beams.add(ExtractedBeam(pos, color))
}