package foo.starred.athen.events

import net.hypixel.data.type.ServerType
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.location.area.base.ISkyBlockArea
import foo.starred.athen.events.core.Event

sealed class LocationEvent {
    sealed class Hypixel {
        data class Server(
            val name: String,
            val type: ServerType?,
            val lobby: String?,
            val mode: String?,
            val map: String?,
        ) : Event()

        data class Island(
            val old: SkyBlockIsland?,
            val new: SkyBlockIsland?
        ) : Event()

        data class Area(
            val old: ISkyBlockArea,
            val new: ISkyBlockArea
        ) : Event()
    }

    sealed class SkyBlock {
        data object Connect : Event()

        data object Disconnect : Event()
    }

    sealed class Server : Event() {
        data object Connect : Server()

        data object Disconnect : Server()
    }
}
