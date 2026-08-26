package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.MotionBlur;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void athen$renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!MotionBlur.INSTANCE.getEnabled()) return;

        MotionBlur.fn();
    }
}
