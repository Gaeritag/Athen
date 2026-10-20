package foo.starred.athen.events

import foo.starred.athen.api.dungeon.data.DungeonFloor
import foo.starred.athen.api.dungeon.data.DungeonPlayer
import foo.starred.athen.events.core.AthenEvent
import net.minecraft.world.item.ItemStack

sealed class DungeonEvent {
    data class Start(
        val floor: DungeonFloor
    ) : AthenEvent()

    data class End(
        val floor: DungeonFloor
    ) : AthenEvent()

    data class Enter(
        val floor: DungeonFloor
    ) : AthenEvent()

    sealed class Player {
        data class Death(
            val player: DungeonPlayer
        ) : AthenEvent()
    }

    sealed class Terminal {
        data object Open : AthenEvent()

        data object Close : AthenEvent()

        data class Update(
            val items: List<ItemStack>
        ) : AthenEvent()
    }
}
