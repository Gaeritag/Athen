package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import foo.starred.athen.modules.impl.render.VisualWords;
import foo.starred.snowbird.api.text.replacer.AbstractTextReplacer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {
    @Unique
    private static int athen$last = -1;

    @Unique
    private static final LongOpenHashSet athen$unmodified = new LongOpenHashSet(1024);

    @ModifyReturnValue(method = "getVisualOrder(Lnet/minecraft/network/chat/FormattedText;)Lnet/minecraft/util/FormattedCharSequence;", at = @At("RETURN"))
    private FormattedCharSequence athen$getVisualOrder(FormattedCharSequence original, FormattedText logicalOrderText) {
        if (original == null) return null;
        if (!(logicalOrderText instanceof Component component)) return original;
        if (!VisualWords.INSTANCE.getEnabled()) return original;
        if (VisualWords.words.getMap0().isEmpty()) return original;

        final int version = VisualWords.words.getVersion();
        if (athen$last != version) {
            athen$unmodified.clear();
            athen$last = version;
        }

        final String string = component.getString();
        final int hash0 = athen$hash(component);
        final long key = ((long) string.hashCode() << 32) | (hash0 & 0xFFFFFFFFL);

        if (athen$unmodified.contains(key)) {
            return original;
        }

        final int hash1 = (string.hashCode() ^ hash0) & 4095;
        final AbstractTextReplacer.Companion.Entry entry = VisualWords.words.getEntries()[hash1];
        if (entry.version == version && entry.style == hash0 && string.equals(entry.string)) {
            return entry.sequence;
        }

        final Component replaced = VisualWords.words.fn(component);
        if (replaced == component) {
            if (athen$unmodified.size() >= 4096) athen$unmodified.clear();
            athen$unmodified.add(key);
            return original;
        }

        final FormattedCharSequence sequence = VisualWords.words.fn(original);
        entry.version = version;
        entry.string = string;
        entry.style = hash0;
        entry.sequence = sequence;
        return sequence;
    }

    @Unique
    private static int athen$hash(Component component) {
        int hash = athen$hash(component.getStyle());
        final List<Component> siblings = component.getSiblings();

        for (Component sibling : siblings) {
            hash = 31 * hash + athen$hash(sibling);
        }

        return hash;
    }

    @Unique
    private static int athen$hash(Style style) {
        if (style.isEmpty()) return 0;
        int flags = (style.isBold() ? 1 : 0) | (style.isItalic() ? 2 : 0) | (style.isUnderlined() ? 4 : 0) | (style.isStrikethrough() ? 8 : 0) | (style.isObfuscated() ? 16 : 0);
        int color = style.getColor() != null ? style.getColor().getValue() : -1;
        return flags ^ (color * 31);
    }
}
