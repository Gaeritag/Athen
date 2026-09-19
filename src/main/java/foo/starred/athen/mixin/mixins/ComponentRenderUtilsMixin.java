package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.VisualWords;
import foo.starred.snowbird.internal.misc.DonatorTextReplacer;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ComponentRenderUtils.class)
public class ComponentRenderUtilsMixin {
    @ModifyVariable(method = "wrapComponents", at = @At("HEAD"), argsOnly = true)
    private static FormattedText athen$wrapComponents(FormattedText message) {
        if (!VisualWords.INSTANCE.getEnabled()) return message;
        if (VisualWords.words.getMap0().isEmpty()) return message;
        if (!(message instanceof Component component)) return message;

        return DonatorTextReplacer.INSTANCE.fn(component);
    }
}
