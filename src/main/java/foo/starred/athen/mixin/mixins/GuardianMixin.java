package foo.starred.athen.mixin.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import foo.starred.athen.ducks.entity.guardian.GuardianDuck;
import foo.starred.athen.modules.impl.slayer.EndermanLaserHider;

@Mixin(Guardian.class)
public class GuardianMixin implements GuardianDuck {
    @Unique
    private int athen$hide = -1;

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 1))
    private void athen$aiStep(Level instance, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
        if (!EndermanLaserHider.INSTANCE.getEnabled()) {
            original.call(instance, particle, x, y, z, xSpeed, ySpeed, zSpeed);
            return;
        }

        if (athen$hide == -1) {
            athen$hide = EndermanLaserHider.fn(self()) ? 1 : 0;
        }

        if (athen$hide != 1) {
            original.call(instance, particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Unique
    private Guardian self() {
        return (Guardian) (Object) this;
    }

    @Override
    public int athen$hide() {
        if (athen$hide == -1) athen$hide = EndermanLaserHider.fn(self()) ? 1 : 0;
        return athen$hide;
    }
}
