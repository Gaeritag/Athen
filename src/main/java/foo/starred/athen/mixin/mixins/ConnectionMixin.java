package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.PacketEvent;
import foo.starred.athen.events.TickEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Connection.class, priority = Integer.MIN_VALUE) // why min value? it's for the features to not break when other mods cancel the packet.
public class ConnectionMixin {
    @Inject(
            method = "channelRead0*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"),
            cancellable = true
    )
    private void athen$channelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundPingPacket p && p.getId() != 0) TickEvent.Server.INSTANCE.post();
        if (new PacketEvent.Receive(packet).post()) ci.cancel();
    }

    @Inject(
            method = "sendPacket(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void athen$sendPacket(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean bl, CallbackInfo ci) {
        if (new PacketEvent.Send(packet).post()) ci.cancel();
    }
}