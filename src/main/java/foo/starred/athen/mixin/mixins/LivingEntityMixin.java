package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    private void athen$die(DamageSource source, CallbackInfo ci) {
        new EntityEvent.Death(athen$self()).post();
    }

    @Unique
    private LivingEntity athen$self() {
        return (LivingEntity) (Object) this;
    }
}
