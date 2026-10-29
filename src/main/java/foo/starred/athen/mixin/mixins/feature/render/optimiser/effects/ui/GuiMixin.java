package foo.starred.athen.mixin.mixins.feature.render.optimiser.effects.ui;

import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'gui.Gui.class' -> 'gui.Hud.class'
@Mixin(net.minecraft.client.gui.Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void athen$renderEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!RenderOptimiser.getEffects()) return;

        ci.cancel();
    }
}
