package foo.starred.athen.events.dispatcher

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.mainThread
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

@Priority
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
    }
}