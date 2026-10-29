package foo.starred.athen.mixin.mixins.feature.render.optimiser.glow;

import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void athen$isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!RenderOptimiser.getGlow()) return;

        cir.setReturnValue(false);
    }
}
