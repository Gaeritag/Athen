package foo.starred.athen.mixin.mixins;

import foo.starred.athen.modules.impl.render.MotionBlur;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.3 {
/*import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
*///? } else {
import net.minecraft.client.DeltaTracker;
//? }

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    //? if >= 26.3 {
    /*@Inject(method = "render3dHud", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/CommandEncoder;clearDepthTexture(Lcom/mojang/renderpearl/api/textures/GpuTexture;D)V"))
    private void athen$renderLevel(CameraRenderState cameraState, PlayerRenderState playerState, OptionsRenderState optionsState, boolean consistentDepthRequired, CallbackInfo ci) {
    *///? } else {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void athen$renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
    //? }
        if (!MotionBlur.INSTANCE.getEnabled()) return;

        MotionBlur.fn();
    }
}
