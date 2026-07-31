package foo.starred.athen.events

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import foo.starred.athen.events.core.Event

data class BlockEvent(
    val old: BlockState,
    val new: BlockState,
    val pos: BlockPos
) : Event()