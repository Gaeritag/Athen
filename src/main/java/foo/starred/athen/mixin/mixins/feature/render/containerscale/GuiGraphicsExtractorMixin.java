package foo.starred.athen.mixin.mixins.feature.render.containerscale;

import foo.starred.athen.modules.impl.render.ContainerScale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >= 26.2 'Quaternionf' -> 'Quaternionfc'
import org.joml.Quaternionf;
//~ if >= 26.2 'Vector3f' -> 'Vector3fc'
import org.joml.Vector3f;

@Mixin(value = GuiGraphicsExtractor.class, priority = Integer.MAX_VALUE)
public class GuiGraphicsExtractorMixin {
    @Inject(method = "entity", at = @At("HEAD"), cancellable = true)
    //~ if >= 26.2 'Vector3f' -> 'Vector3fc'
    //~ if >= 26.2 'Quaternionf' -> 'Quaternionfc'
    private void athen$entity(EntityRenderState renderState, float scale, Vector3f translation, Quaternionf rotation, Quaternionf overrideCameraAngle, int x0, int y0, int x1, int y1, CallbackInfo ci) {
        if (!ContainerScale.getBool()) return;

        double scale1 = ContainerScale.INSTANCE.getScale();
        double x2 = ContainerScale.getX0();
        double y2 = ContainerScale.getY0();

        int x3 = (int) (x2 + (x0 - x2) * scale1);
        int y3 = (int) (y2 + (y0 - y2) * scale1);
        int x4 = (int) (x2 + (x1 - x2) * scale1);
        int y4 = (int) (y2 + (y1 - y2) * scale1);
        float scale2 = (float) (scale * scale1);

        renderState.lightCoords = 15728880;
        self().guiRenderState.addPicturesInPictureState(new GuiEntityRenderState(renderState, translation, rotation, overrideCameraAngle, x3, y3, x4, y4, scale2, self().scissorStack.peek()));

        ci.cancel();
    }

    @Unique
    private GuiGraphicsExtractor self() {
        return (GuiGraphicsExtractor) (Object) this;
    }
}
