package foo.starred.athen.mixin.mixins.duck.entity.render;

import foo.starred.athen.ducks.entity.render.EntityRenderStateDuck;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateDuckImpl implements EntityRenderStateDuck {
    @Unique
    private Entity athen$entity;

    @Override
    public Entity athen$entity() {
        return this.athen$entity;
    }

    @Override
    public void athen$entity(Entity entity) {
        this.athen$entity = entity;
    }
}
