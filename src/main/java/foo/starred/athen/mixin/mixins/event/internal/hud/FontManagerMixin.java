package foo.starred.athen.mixin.mixins.event.internal.hud;

import foo.starred.athen.config.hud.impl.HudRenderer;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontManager.class)
public class FontManagerMixin {
    @Inject(method = "apply", at = @At("RETURN"))
    private void athen$onFontReload(FontManager.Preparation preparations, ProfilerFiller profiler, CallbackInfo ci) {
        HudRenderer.INSTANCE.constrain();
    }
}
