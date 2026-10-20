package foo.starred.athen.events.dispatcher

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.events.*
import foo.starred.athen.events.core.on
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.client.extension.mainThread
import foo.starred.kontext.core.event.impl.KontextEvents
import foo.starred.parallax.api.events.ParallaxEvents
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import net.minecraft.world.entity.LivingEntity

@LoadableAthenModule
object GenericEventDispatcher {
    init {
        on<PacketEvent.Receive, ClientboundSystemChatPacket> {
            mainThread {
                (if (this@on.overlay) MessageEvent.ActionBar(content) else MessageEvent.Chat.Receive(content)).post()
            }
        }

        on<PacketEvent.Process.Pre, ClientboundSetTitleTextPacket> {
            if (MessageEvent.Title.Main(text).post()) it.cancel()
        }

        on<PacketEvent.Process.Pre, ClientboundSetSubtitleTextPacket> {
            if (MessageEvent.Title.Sub(text).post()) it.cancel()
        }

        on<PacketEvent.Process.Pre, ClientboundSetEquipmentPacket> {
            val level = client.level ?: return@on
            val entity = level.getEntity(entity) as? LivingEntity ?: return@on

            EntityEvent.Update.Equipment(entity).post()
        }

        KontextEvents.Scoreboard.UPDATE.register { event ->
            ScoreboardEvent.Update(event.lines0, event.lines1, event.components0, event.components1).post()
        }

        KontextEvents.TabList.UPDATE.register { event ->
            TabListEvent.Change(event.lines0, event.components1).post()
        }

        ParallaxEvents.EXTRACT.register {
            WorldRenderEvent.Extract.post()
        }
    }
}
