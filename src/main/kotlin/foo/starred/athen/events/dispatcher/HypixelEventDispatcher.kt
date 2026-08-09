package foo.starred.athen.events.dispatcher

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.LocationEvent
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.fabric.event.HypixelModAPICallback
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

@Priority
object HypixelEventDispatcher {
    init {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)

        HypixelModAPICallback.EVENT.register { event ->
            if (event !is ClientboundLocationPacket) return@register
            LocationEvent.Hypixel.Server(event.serverName, event.serverType.getOrNull(), event.lobbyName.getOrNull(), event.mode.getOrNull(), event.map.getOrNull()).post()
        }
    }
}