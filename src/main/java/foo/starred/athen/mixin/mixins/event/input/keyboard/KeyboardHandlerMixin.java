package foo.starred.athen.mixin.mixins.event.input.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import foo.starred.athen.events.InputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void athen$keyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (event.key() == InputConstants.UNKNOWN.getValue()) return;

        if (action == 1) {
            if (new InputEvent.Keyboard.Press(event).post()) ci.cancel();
            return;
        }

        if (action == 0) {
            new InputEvent.Keyboard.Release(event).post();
        }
    }
}
