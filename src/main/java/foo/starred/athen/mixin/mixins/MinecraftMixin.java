package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.GuiEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'client.Minecraft' -> 'client.gui.Gui'
@Mixin(net.minecraft.client.Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    //~ if >= 26.2 'public' -> 'private'
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void athen$setScreen(Screen screen, CallbackInfo ci) {
        Screen old = this.screen;
        if (old == screen) return;

        if (old != null) {
            new GuiEvent.Close.Any(old).post();
            if (old instanceof AbstractContainerScreen<?> c) new GuiEvent.Close.Container(c).post();
        }

        if (screen != null) {
            new GuiEvent.Open.Any(screen).post();
            if (screen instanceof AbstractContainerScreen<?> c) new GuiEvent.Open.Container(c).post();
        }
    }
}
