//? if >= 26.3 {
/*package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.PlayerEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void athen$dropItem(LocalPlayer player, boolean all, CallbackInfo ci) {
        final ItemStack c = player.inventoryMenu.getSlot(player.getInventory().getSelectedSlot() + 36).getItem();

        if (c == ItemStack.EMPTY) return;
        if (!(new PlayerEvent.Drop(c, false).post())) return;

        ci.cancel();
    }
}
*///? }
