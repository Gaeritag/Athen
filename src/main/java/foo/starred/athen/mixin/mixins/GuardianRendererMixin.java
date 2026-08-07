package foo.starred.athen.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import foo.starred.athen.ducks.entity.EntityRenderStateDuck;
import foo.starred.athen.ducks.entity.guardian.GuardianDuck;
import foo.starred.athen.modules.impl.slayer.EndermanLaserHider;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
//~ if >= 26.1 'CameraRenderState' -> 'level.CameraRenderState'
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuardianRenderer.class)
public class GuardianRendererMixin {
    //~ if >= 26.1 'CameraRenderState;)V' -> 'level/CameraRenderState;)V'
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/GuardianRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"), cancellable = true)
    private void athen$submit(GuardianRenderState guardianRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!EndermanLaserHider.INSTANCE.getEnabled()) return;

        final Guardian a = (Guardian) ((EntityRenderStateDuck) guardianRenderState).athen$getEntity();
        if (a == null) return;

        final int b = ((GuardianDuck) a).athen$hide();
        if (b != 1) return;

        ci.cancel();
    }
}
