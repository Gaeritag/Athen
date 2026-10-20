package foo.starred.athen.api.slayers.resolver.impl

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.network.websocket.base.IWebSocket
import foo.starred.athen.api.network.websocket.data.SocketPacket
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.api.slayers.resolver.base.GenericSlayerBossResolver
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.client.extension.mainThread

@LoadableAthenModule
object WebSocketSlayerBossResolver : IWebSocket {
    init {
        on<SlayerEvent.Boss.Spawn> {
            if (!ModSettings.shareSlayer.value) return@on
            if (!slayerInfo.owned) return@on
            if (!auth) return@on

            val type = (slayerInfo.type as? SlayerBoss)?.name ?: return@on
            val tier = slayerInfo.tier?.int ?: return@on

            `socket$send`(SocketPacket.Slayer.ServerBound.Spawn.id, "b" to "${entity.id},$type,$tier,${slayerInfo.phase}")
        }

        on<SlayerEvent.Boss.Death> {
            if (!ModSettings.shareSlayer.value) return@on
            if (!slayerInfo.owned) return@on
            if (!auth) return@on

            `socket$send`(SocketPacket.Slayer.ServerBound.Death.id, "b" to "${entity.id}")
        }

        on<InternalEvent.WebSocket.Message> {
            if (!ModSettings.shareSlayer.value) return@on

            when (id) {
                SocketPacket.Slayer.ClientBound.Spawn.id -> {
                    val body = body ?: return@on

                    val split = body.split(",")
                    if (split.size < 4) return@on

                    val id = split[0].toIntOrNull() ?: return@on
                    val type1 = runCatching { SlayerBoss.valueOf(split[1]) }.getOrNull() ?: return@on
                    val tier1 = split[2].toIntOrNull()?.let { a -> SlayerTier.entries.find { b -> b.int == a } } ?: return@on
                    val phase1 = split[3].toIntOrNull() ?: return@on
                    val owner1 = name ?: split.getOrNull(4) ?: return@on

                    mainThread {
                        val entity = client.level?.getEntity(id) ?: return@mainThread

                        GenericSlayerBossResolver.spawn(SlayerAPI.bosses.computeIfAbsent(entity, ::SlayerInfo).apply {
                            type = type1
                            tier = tier1
                            phase = phase1
                            owner = owner1
                        })
                    }
                }

                SocketPacket.Slayer.ClientBound.Death.id -> {
                    val body = body ?: return@on

                    mainThread {
                        val id = body.toIntOrNull() ?: return@mainThread
                        val entity = SlayerAPI.bosses.keys.find { it.id == id } ?: client.level?.getEntity(id) ?: return@mainThread
                        val info = SlayerAPI.bosses.remove(entity) ?: return@mainThread

                        SlayerAPI.logged.remove(id)
                        SlayerEvent.Boss.Death(entity, info).post()
                    }
                }
            }
        }
    }
}
