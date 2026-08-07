package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "getBuffer", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static FogRenderer.FogMode athen$getBuffer(FogRenderer.FogMode fogMode) {
        if (RenderOptimiser.getFog()) return FogRenderer.FogMode.NONE;
        return fogMode;
    }
}