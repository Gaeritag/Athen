package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.EntityEvent;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.mixins.accessors.LivingEntityAccessor;

@Mixin(SynchedEntityData.class)
public class SynchedEntityDataMixin {
    @Final
    @Shadow
    private SyncedDataHolder entity;

    @Unique
    private Float last;

    @Inject(method = "assignValue", at = @At("HEAD"))
    private <T> void athen$assignValue$pre(SynchedEntityData.DataItem<T> dataItem, SynchedEntityData.DataValue<?> item, CallbackInfo ci) {
        if (dataItem.getAccessor() != LivingEntityAccessor.skyblockapi$getDataHealth()) return;
        if (!(this.entity instanceof LivingEntity living)) return;
        last = living.getHealth();
    }

    @Inject(method = "assignValue", at = @At("TAIL"))
    private <T> void athen$assignValue$post(SynchedEntityData.DataItem<T> dataItem, SynchedEntityData.DataValue<?> item, CallbackInfo ci) {
        if (dataItem.getAccessor() != LivingEntityAccessor.skyblockapi$getDataHealth()) return;
        if (!(this.entity instanceof LivingEntity living)) return;

        float v = (Float) item.value();
        boolean b = v >= living.getMaxHealth();

        if (!b && (last == null || last.equals(v))) return;
        new EntityEvent.Update.Health(living, last, v).post();
    }
}