package foo.starred.athen.mixin.mixins.feature.render.radialmenu;

import foo.starred.athen.modules.impl.render.radial.RadialMenu;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'gui.Gui.class' -> 'gui.Hud.class'
@Mixin(net.minecraft.client.gui.Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void athen$renderCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!RadialMenu.INSTANCE.getEnabled()) return;
        if (!RadialMenu.INSTANCE.getOpen().getValue()) return;

        ci.cancel();
    }
}
