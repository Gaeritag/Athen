package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import foo.starred.athen.modules.impl.render.VisualWords;
import foo.starred.snowbird.api.text.replacer.AbstractTextReplacer;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(StringSplitter.class)
public abstract class StringSplitterMixin {
    @Unique
    private static final long[] athen$widths = new long[4096];

    @Shadow
    public abstract float stringWidth(FormattedCharSequence text);

    @ModifyReturnValue(method = "stringWidth(Lnet/minecraft/network/chat/FormattedText;)F", at = @At("RETURN"))
    private float athen$stringWidth(float original, FormattedText text) {
        if (!(text instanceof Component component)) return original;

        final String string = component.getString();
        final int hash0 = snowbird$hash(component);
        final int hash1 = (string.hashCode() ^ hash0) & 4095;

        final int version = VisualWords.words.getVersion();
        final AbstractTextReplacer.Companion.Entry entry = VisualWords.words.getEntries()[hash1];

        if (entry.version == version && entry.style == hash0 && string.equals(entry.string)) {
            final int version0 = version ^ string.hashCode() ^ hash0;
            final long packed = athen$widths[hash1];

            if ((int) (packed >>> 32) == version0 && packed != 0L) {
                return Float.intBitsToFloat((int) packed);
            }

            final float width = this.stringWidth(entry.sequence);
            athen$widths[hash1] = ((long) version0 << 32) | (Float.floatToIntBits(width) & 0xFFFFFFFFL);
            return width;
        }

        return original;
    }

    @Unique
    private static int snowbird$hash(Component component) {
        int hash = snowbird$hash(component.getStyle());
        final List<Component> siblings = component.getSiblings();

        for (Component sibling : siblings) {
            hash = 31 * hash + snowbird$hash(sibling);
        }

        return hash;
    }

    @Unique
    private static int snowbird$hash(Style style) {
        if (style.isEmpty()) return 0;
        int flags = (style.isBold() ? 1 : 0) | (style.isItalic() ? 2 : 0) | (style.isUnderlined() ? 4 : 0) | (style.isStrikethrough() ? 8 : 0) | (style.isObfuscated() ? 16 : 0);
        int color = style.getColor() != null ? style.getColor().getValue() : -1;
        return flags ^ (color * 31);
    }
}
