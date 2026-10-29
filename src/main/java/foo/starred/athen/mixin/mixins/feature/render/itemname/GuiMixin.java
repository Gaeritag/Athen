package foo.starred.athen.mixin.mixins.feature.render.itemname;

import foo.starred.athen.modules.impl.render.ItemNamePosition;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//~ if >= 26.2 'gui.Gui.class' -> 'gui.Hud.class'
@Mixin(net.minecraft.client.gui.Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void athen$renderSelectedItemName$0(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!ItemNamePosition.INSTANCE.getEnabled()) return;
        if (ItemNamePosition.INSTANCE.getHud().getState().getValue()) return;

        ci.cancel();
    }

    @ModifyArgs(method = "extractSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;textWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"))
    private void athen$renderSelectedItemName$1(Args args) {
        if (!ItemNamePosition.INSTANCE.getEnabled()) return;

        int width = args.get(4);

        args.set(2, ItemNamePosition.x() - (width / 2));
        args.set(3, ItemNamePosition.y());
    }
}
