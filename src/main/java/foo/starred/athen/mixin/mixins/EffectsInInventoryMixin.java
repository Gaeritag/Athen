package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void athen$renderEffects(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!RenderOptimiser.getEffects()) return;

        ci.cancel();
    }
}
