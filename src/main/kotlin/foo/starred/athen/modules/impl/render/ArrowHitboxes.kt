package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.parallax.api.primitives.ParallaxBox
import net.minecraft.client.renderer.entity.state.ArrowRenderState

@LoadableAthenModule
object ArrowHitboxes : Module(
    "Arrow hitboxes",
    "Shows the hitboxes for arrows",
    ConfigCategory.RENDER
) {
    private val color by config.colorPicker("Color", MochaColorScheme.Green.argb)
    private val thickness by config.slider("Thickness", 2f, 1f, 10f)

    init {
        on<WorldRenderEvent.Entity> {
            if (renderState !is ArrowRenderState) return@on
            val entity = entity ?: return@on

            ParallaxBox.frame(entity.renderAABB(), color, thickness)
        }
    }
}
