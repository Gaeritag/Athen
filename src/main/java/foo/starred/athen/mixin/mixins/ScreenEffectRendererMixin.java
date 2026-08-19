package foo.starred.athen.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import foo.starred.athen.modules.impl.render.RenderOptimiser;
//~ if >= 26.2 'MultiBufferSource;' -> 'SubmitNodeCollector;'
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    //~ if >= 26.2 'renderFire' -> 'submitFire'
    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    //~ if >= 26.2 'MultiBufferSource' -> 'SubmitNodeCollector'
    private static void athen$renderFire(PoseStack poseStack, MultiBufferSource consumers, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (RenderOptimiser.getFire()) ci.cancel();
    }
}