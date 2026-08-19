package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import foo.starred.athen.modules.impl.render.VisualWords;
import foo.starred.snowbird.handlers.minecraft.AbstractWords;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(StringSplitter.class)
public abstract class StringSplitterMixin {
    @Unique
    private static final long[] athen$widths = new long[512];

    @Shadow
    public abstract float stringWidth(FormattedCharSequence text);

    @ModifyReturnValue(method = "stringWidth(Lnet/minecraft/network/chat/FormattedText;)F", at = @At("RETURN"))
    private float athen$stringWidth(float original, FormattedText text) {
        if (!(text instanceof Component component)) return original;
        if (!VisualWords.INSTANCE.getEnabled()) return original;
        if (VisualWords.words.getMap0().isEmpty()) return original;

        final String string = component.getString();
        final Style style = component.getStyle();
        final int hash = (string.hashCode() ^ style.hashCode()) & 511;

        final int version = VisualWords.words.getVersion();
        final AbstractWords.Companion.Entry entry = VisualWords.words.getEntries()[hash];

        if (entry.version == version && string.equals(entry.string) && Objects.equals(style, entry.style)) {
            final int version0 = version ^ string.hashCode();
            final long packed = athen$widths[hash];

            if ((int) (packed >>> 32) == version0 && packed != 0L) {
                return Float.intBitsToFloat((int) packed);
            }

            final float width = this.stringWidth(entry.sequence);
            athen$widths[hash] = ((long) version0 << 32) | (Float.floatToIntBits(width) & 0xFFFFFFFFL);
            return width;
        }

        return original;
    }
}