package foo.starred.athen.mixin.mixins.feature.render.tweaks;

import foo.starred.athen.modules.impl.render.RenderTweaks;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static foo.starred.kontext.abstraction.client.extension.MinecraftClientExtensionsKt.client;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Avatar;D)Z", at = @At("HEAD"), cancellable = true)
    private void athen$shouldShowName(Avatar entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        if (!RenderTweaks.getNametag()) return;

        final LocalPlayer a = client.player;
        if (a == null) return;
        if (a != entity) return;

        cir.setReturnValue(true);
    }
}
