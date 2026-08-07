package foo.starred.athen.events

import foo.starred.athen.api.dungeon.enums.DungeonPlayer
import foo.starred.athen.events.core.Event
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor

sealed class DungeonEvent {
    data class Start(
        val floor: DungeonFloor
    ) : Event()

    data class End(
        val floor: DungeonFloor
    ) : Event()

    data class Enter(
        val floor: DungeonFloor
    ) : Event()

    sealed class Player {
        data class Death(
            val player: DungeonPlayer
        ) : Event()
    }

    sealed class Terminal {
        data object Open : Event()

        data object Close : Event()

        data class Update(
            val items: List<ItemStack>
        ) : Event()
    }
}
