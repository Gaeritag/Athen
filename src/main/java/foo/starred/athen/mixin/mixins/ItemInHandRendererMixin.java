package foo.starred.athen.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import foo.starred.athen.modules.impl.render.RenderOptimiser;
//~ if >= 26.3 'ItemInHandRenderer' -> 'FirstPersonHandsAndItemsRenderer'
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.3
//import net.minecraft.client.renderer.state.level.PlayerRenderState;

//~ if >= 26.3 'ItemInHandRenderer' -> 'FirstPersonHandsAndItemsRenderer'
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    //? if >= 26.3 {
    /*private void athen$renderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm, PlayerRenderState playerRenderState, CallbackInfo ci) {
    *///?} else {
    private void athen$renderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm, CallbackInfo ci) {
    //?}
        if (RenderOptimiser.getArm()) ci.cancel();
    }

}
