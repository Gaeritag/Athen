package foo.starred.athen.mixin.mixins.feature.render.tooltip.generic;

import foo.starred.athen.modules.impl.render.tooltip.ScrollableTooltip;
import foo.starred.athen.modules.impl.render.tooltip.custom.CustomTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiGraphicsExtractor.class, priority = Integer.MAX_VALUE)
public class GuiGraphicsExtractorMixin {
    @Inject(method = "tooltip", at = @At("HEAD"), cancellable = true)
    //? if >= 26.3 {
    /*private void athen$renderTooltip(Font font, List<ClientTooltipComponent> lines, int x, int y, ClientTooltipPositioner positioner, Identifier style, boolean extraSpaceAfterFirstLine, CallbackInfo ci) {
    *///? } else {
    private void athen$renderTooltip(Font font, List<ClientTooltipComponent> lines, int x, int y, ClientTooltipPositioner positioner, Identifier style, CallbackInfo ci) {
    //? }
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
