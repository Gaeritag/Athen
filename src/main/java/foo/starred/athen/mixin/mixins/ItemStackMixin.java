package foo.starred.athen.mixin.mixins;

import foo.starred.athen.ducks.item.ItemStackDuck;
import kotlin.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackDuck {
    @Unique
    private Pair<@NotNull List<Component>, @NotNull List<Component>> athen$tooltip;

    @Override
    public @Nullable Pair<@NotNull List<@NotNull Component>, @NotNull List<@NotNull Component>> athen$cache$tooltip() {
        return athen$tooltip;
    }

    @Override
    public void athen$cache$tooltip(@Nullable Pair<@NotNull List<@NotNull Component>, @NotNull List<@NotNull Component>> pair) {
        athen$tooltip = pair;
    }
}
