package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import foo.starred.athen.modules.impl.render.VisualWords;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @Unique
    private FormattedCharSequence athen$cached;

    @Unique
    private String athen$last;

    @Unique
    private int athen$position = -1;

    @Unique
    private int athen$version = -1;

    @ModifyReturnValue(method = "applyFormat", at = @At("RETURN"))
    private FormattedCharSequence athen$applyFormat(FormattedCharSequence original, @Local(argsOnly = true) String text, @Local(argsOnly = true) int offset) {
        if (!VisualWords.INSTANCE.getEnabled()) return original;
        if (VisualWords.words.getMap0().isEmpty()) return original;

        final int version = VisualWords.words.getVersion();
        if (offset == this.athen$position && this.athen$version == version && Objects.equals(text, this.athen$last) && this.athen$cached != null) return this.athen$cached;

        this.athen$last = text;
        this.athen$position = offset;
        this.athen$version = version;
        return this.athen$cached = VisualWords.words.fn(original);
    }
}