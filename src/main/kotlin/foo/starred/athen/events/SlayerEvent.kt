package foo.starred.athen.events

import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.events.core.Event
import net.minecraft.world.entity.Entity

sealed class SlayerEvent {
    sealed class Boss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()
    }

    sealed class Miniboss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()
    }

    sealed class Demon {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : Event()
    }

    sealed class Quest {
        data object Start : Event()

        data object End : Event()
    }

    sealed class Reset : Event() {
        data object QuestFail : Reset()

        data object ServerChange : Reset()

        data object Any : Reset()
    }
}