package foo.starred.athen.mixin.mixins;

import foo.starred.athen.ducks.entity.EntityRenderStateDuck;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateDuck {
    @Unique
    private Entity athen$entity;

    @Override
    public Entity athen$getEntity() {
        return this.athen$entity;
    }

    @Override
    public void athen$setEntity(Entity entity) {
        this.athen$entity = entity;
    }
}