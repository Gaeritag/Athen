package foo.starred.athen.mixin.mixins.event.render.gui.container;

import foo.starred.athen.events.GuiEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(method = "extractSlots", at = @At("HEAD"), cancellable = true)
    private void athen$extractSlots$pre(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (new GuiEvent.Slots.Render.Menu.Start(graphics).post()) ci.cancel();
    }

    @Inject(method = "extractSlots", at = @At("HEAD"))
    private void athen$extractSlots$post(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        new GuiEvent.Slots.Render.Menu.End(graphics).post();
    }

    @Inject(method = "extractSlot", at = @At("HEAD"), cancellable = true)
    private void athen$extractSlot$pre(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (new GuiEvent.Slots.Render.Any.Pre(graphics, slot).post()) ci.cancel();
    }

    @Inject(method = "extractSlot", at = @At("RETURN"))
    private void athen$extractSlot$post(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        new GuiEvent.Slots.Render.Any.Post(graphics, slot).post();
    }
}
