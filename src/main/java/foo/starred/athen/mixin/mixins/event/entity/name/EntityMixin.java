package foo.starred.athen.mixin.mixins.event.entity.name;

import foo.starred.athen.events.EntityEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    @Final
    private static EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;

    @Shadow
    @Nullable
    public abstract Component getCustomName();

    @Inject(method = "setCustomName", at = @At("RETURN"))
    private void athen$setCustomName(Component name, CallbackInfo ci) {
        Entity entity = athen$self();

        if (!(entity instanceof ArmorStand stand)) return;
        if (name == null) return;

        new EntityEvent.Update.Named(name, stand).post();
    }

    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
    private void athen$onSyncedDataUpdated(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        Entity entity = athen$self();

        if (!(entity instanceof ArmorStand stand)) return;
        if (accessor != DATA_CUSTOM_NAME) return;

        Component component = getCustomName();
        if (component == null) return;

        new EntityEvent.Update.Named(component, stand).post();
    }

    @Unique
    private Entity athen$self() {
        return (Entity) (Object) this;
    }
}
