package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.GuiEvent;
import foo.starred.athen.modules.impl.render.tooltip.ScrollableTooltip;
import foo.starred.athen.modules.impl.render.tooltip.custom.CustomTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiGraphicsExtractor.class, priority = Integer.MAX_VALUE)
public class GuiGraphicsMixin {
    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void athen$renderItem(LivingEntity owner, Level level, ItemStack itemStack, int x, int y, int seed, CallbackInfo ci) {
        new GuiEvent.Items.Render.Pre(self(), itemStack, x, y).post();
    }

    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void athen$renderItemDecorations(Font font, ItemStack itemStack, int x, int y, String countText, CallbackInfo ci) {
        new GuiEvent.Items.Render.Post(self(), itemStack, x, y).post();
    }

    @Inject(method = "tooltip", at = @At("HEAD"), cancellable = true)
    private void athen$renderTooltip(Font font, List<ClientTooltipComponent> lines, int x, int y, ClientTooltipPositioner positioner, Identifier style, CallbackInfo ci) {
        boolean a = CustomTooltip.INSTANCE.getEnabled();
        boolean b = ScrollableTooltip.INSTANCE.getEnabled();

        if (!a && !b) return;
        ci.cancel();

        if (a) CustomTooltip.render(self(), font, lines, x, y, positioner);
        else ScrollableTooltip.fn(self(), font, lines, x, y, positioner, style);
    }

    @Unique
    private GuiGraphicsExtractor self() {
        return (GuiGraphicsExtractor) (Object) this;
    }
}
