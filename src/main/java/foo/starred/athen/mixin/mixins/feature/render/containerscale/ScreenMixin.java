package foo.starred.athen.mixin.mixins.feature.render.containerscale;

import foo.starred.athen.modules.impl.render.ContainerScale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "extractTransparentBackground", at = @At("HEAD"), cancellable = true)
    private void athen$extractBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!ContainerScale.getBool()) return;

        ci.cancel();
    }
}
