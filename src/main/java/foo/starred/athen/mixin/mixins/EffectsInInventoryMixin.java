package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.RenderOptimiser;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {
    //~ if >= 26.1 'render' -> 'extractRenderState'
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void athen$renderEffects(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!RenderOptimiser.getEffects()) return;

        ci.cancel();
    }
}
