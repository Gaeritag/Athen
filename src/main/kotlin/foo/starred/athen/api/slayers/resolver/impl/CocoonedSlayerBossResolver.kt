package foo.starred.athen.api.slayers.resolver.impl

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.base.ISlayerType
import foo.starred.athen.api.slayers.resolver.base.GenericSlayerBossResolver
import foo.starred.athen.api.slayers.resolver.base.GenericSlayerBossResolver.spawn
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.core.on
import foo.starred.kontext.abstraction.client.extension.mainThread
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.scheduling.scheduler.extensions.serverTicks
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

@LoadableAthenModule
object CocoonedSlayerBossResolver {
    private var type: ISlayerType? = null
    private var tier: SlayerTier? = null
    private var cocoon: Boolean = false

    init {
        on<SlayerEvent.Boss.Death> {
            if (!slayerInfo.owned) return@on

            type = slayerInfo.type
            tier = slayerInfo.tier
        }

        on<MessageEvent.Chat.Receive> {
            if (stripped.trim() != "YOU COCOONED YOUR SLAYER BOSS") return@on

            cocoon = true
            Scheduler.schedule(140.serverTicks, ::reset)
        }

        on<PacketEvent.Receive, ClientboundSystemChatPacket> {
            if (!cocoon) return@on
            if (overlay) return@on
            if (content.stripped().trim() != "SLAYER QUEST STARTED!") return@on

            val type0 = type ?: return@on
            val tier0 = tier ?: return@on
            val last = GenericSlayerBossResolver.last

            reset()
            mainThread {
                val entity = level?.getEntity(last) ?: return@mainThread

                spawn(SlayerAPI.bosses.computeIfAbsent(entity, ::SlayerInfo).apply {
                    type = type0
                    tier = tier0
                    phase = 1
                    owner = user.name
                })
            }
        }

        on<LocationEvent.Server.Connect> {
            reset()
        }
    }

    private fun reset() {
        type = null
        tier = null
        cocoon = false
    }
}
