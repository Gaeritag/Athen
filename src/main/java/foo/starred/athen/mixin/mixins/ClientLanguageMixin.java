package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import foo.starred.athen.modules.impl.render.VisualWords;
import foo.starred.snowbird.handlers.minecraft.AbstractWords;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {
    @Unique
    private static final int[] athen$unmodified = new int[512];

    @ModifyReturnValue(method = "getVisualOrder(Lnet/minecraft/network/chat/FormattedText;)Lnet/minecraft/util/FormattedCharSequence;", at = @At("RETURN"))
    private FormattedCharSequence athen$getVisualOrder(FormattedCharSequence original, FormattedText logicalOrderText) {
        if (original == null) return null;
        if (!(logicalOrderText instanceof Component component)) return original;
        if (!VisualWords.INSTANCE.getEnabled()) return original;
        if (VisualWords.words.getMap0().isEmpty()) return original;

        final String string = component.getString();
        final Style style = component.getStyle();
        final int hash = (string.hashCode() ^ style.hashCode()) & 511;

        final int version = VisualWords.words.getVersion();
        if (athen$unmodified[hash] == (string.hashCode() ^ version)) return original;

        final AbstractWords.Companion.Entry entry = VisualWords.words.getEntries()[hash];
        if (entry.version == version && string.equals(entry.string) && Objects.equals(style, entry.style)) {
            return entry.sequence;
        }

        Component replaced = VisualWords.words.fn(component);
        if (replaced.getString().equals(component.getString()) && replaced.getStyle().equals(component.getStyle())) {
            athen$unmodified[hash] = string.hashCode() ^ version;
            return original;
        }

        FormattedCharSequence sequence = VisualWords.words.fn(original);
        entry.version = version;
        entry.string = string;
        entry.style = style;
        entry.sequence = sequence;
        return sequence;
    }
}