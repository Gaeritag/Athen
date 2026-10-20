package foo.starred.athen.mixin.mixins;

import foo.starred.athen.events.SoundPlayEvent;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void jec$play(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (sound == null) return;
        if (!(sound instanceof AbstractSoundInstance instance)) return;
        if (instance.getSound() == null) return;


        if (new SoundPlayEvent(sound.getIdentifier(), new Vec3(sound.getX(), sound.getY(), sound.getZ()), sound.getVolume(), sound.getPitch()).post()) cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
    }
}
