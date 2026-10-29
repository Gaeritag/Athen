package foo.starred.athen.mixin.mixins.feature.render.optimiser.effects.fire;

import com.mojang.blaze3d.vertex.PoseStack;
import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'MultiBufferSource;' -> 'SubmitNodeCollector;'
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    //~ if >= 26.2 'renderFire' -> 'submitFire'
    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    //~ if >= 26.2 'MultiBufferSource' -> 'SubmitNodeCollector'
    private static void athen$renderFire(PoseStack poseStack, MultiBufferSource bufferSource, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (!RenderOptimiser.getFire()) return;

        ci.cancel();
    }
}
