@file:Suppress("LocalVariableName")

package foo.starred.athen.api.slayers.resolver.base

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.impl.MessagingAPI.dev
import foo.starred.athen.api.network.websocket.helpers.WebSocketLocationResolver
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.client.extension.mainThread
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.acos

//~ if >= 26.2 'EntityType' -> 'EntityTypes'
import net.minecraft.world.entity.EntityType

@LoadableAthenModule
object GenericSlayerBossResolver {
    private val spawnRegex = Regex("SLAYER BOSS! The (?<type>.+?) (?<tier>[IVXLCDM]+) spawned!")
    //~ if >= 26.2 'EntityType' -> 'EntityTypes'
    private val set = setOf(EntityType.ZOMBIE, EntityType.ENDERMAN, EntityType.SPIDER, EntityType.BLAZE, EntityType.WOLF)

    var last: Int = 0

    init {
        on<PacketEvent.Receive, ClientboundAddEntityPacket> {
            if (type !in set) return@on

            last = id
        }

        on<PacketEvent.Receive, ClientboundSystemChatPacket> {
            if (overlay) return@on
            val text = content.stripped().takeIf { it.startsWith("SLAYER BOSS! The ") } ?: return@on
            val match = spawnRegex.find(text) ?: return@on

            val _type = match.groups["type"]?.value ?: return@on
            val _tier = match.groups["tier"]?.value ?: return@on

            val type0 = SlayerBoss.entries.firstOrNull { _type in it.names } ?: return@on
            val tier0 = SlayerTier.find(_tier) ?: return@on
            val last = last

            mainThread {
                val entity = level?.getEntity(last) ?: return@mainThread

                spawn(SlayerAPI.bosses.computeIfAbsent(entity, ::SlayerInfo).apply {
                    type = type0
                    tier = tier0
                    owner = user.name
                })
            }
        }

        on<TickEvent.Client.End> {
            if (ticks % 10 != 0) return@on

            for (info in SlayerAPI.bosses.values) {
                if (info.type !is SlayerBoss) continue
                if (info.owner != null) continue
                if (!info.entity.isAlive) continue
                if (info.entity.tickCount > 60) continue

                get(info, ModSettings.tickBossCheck)
            }
        }
    }

    fun get(info: SlayerInfo, tick: Boolean = false) {
        if (info.type !is SlayerBoss) return
        if (info.owner != null) return
        if (info.phase == 2 && info.entity.tickCount > 15) return

        val living = info.entity as? LivingEntity ?: return
        val looked = living.looking(info.phase == 2, tick) ?: return

        info.owner = looked.gameProfile.name()
        spawn(info)
    }

    fun spawn(info: SlayerInfo) {
        if (info.owned) {
            SlayerAPI.slayer = info
        }

        if (SlayerAPI.logged.add(info.entity.id)) {
            SlayerEvent.Boss.Spawn(info.entity, info).post()
            "SlayerAPI: Slayer spawned (owner=${info.owner}, phase=${info.phase}, tier=${info.tier}, tickAge=${info.entity.tickCount / 20.0}s)".dev()
        }
    }

    private fun LivingEntity.looking(p2: Boolean = false, tick: Boolean = false, distance: Double = 15.0): Player? {
        val level = client.level ?: return null
        val distance = distance * distance
        val active = SlayerAPI.bosses.values.filter { it.entity.isAlive && (it.type != SlayerBoss.Tarantula || it.phase != 1) }.mapNotNull { it.owner }
        val players = level.players().filter { it.valid(this, distance, active, p2, tick) }

        val angles = players.associateWith { angle(it) }
        val pool = angles.filter { it.value <= 15.0 }.ifEmpty { angles.filter { it.value <= 30.0 } }.ifEmpty { angles.filter { it.value <= 60.0 } }

        return pool.minByOrNull { (v0, v1) -> v1 + (v0.distanceTo(this) * 0.3) }?.key
    }

    private fun Player.valid(boss: Entity, distance: Double, active: List<String>, p2: Boolean = false, tick: Boolean = false): Boolean {
        if (this == client.player && !p2 && !tick) return false
        if (uuid.version() != 4) return false

        val name = gameProfile.name()
        if (name in active) return false
        if (ModSettings.shareSlayer.value && name in WebSocketLocationResolver.users) return false

        return distanceToSqr(boss) <= distance
    }

    private fun LivingEntity.angle(player: Player): Double {
        val vec3 = player.eyePosition.subtract(eyePosition)
        val distance = vec3.length().takeIf { it >= 0.001 } ?: return 0.0

        return Math.toDegrees(acos(getViewVector(1.0f).normalize().dot(vec3.scale(1.0 / distance)).coerceIn(-1.0, 1.0)))
    }
}
