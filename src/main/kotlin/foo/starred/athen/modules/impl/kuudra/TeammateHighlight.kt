package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.parallax.api.primitives.ParallaxBox

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object TeammateHighlight : Module(
    "Teammate highlight",
    "Highlights your teammates in kuudra!",
    ConfigCategory.KUUDRA
) {
    private val lineWidth by config.slider("Line width", 2f, 1f, 10f)
    private val color by config.colorPicker("Color", MochaColorScheme.Green.argb)

    init {
        on<WorldRenderEvent.Extract> {
            for (p in KuudraAPI.teammates) {
                if (p.name == client.user.name) continue
                val e = p.entity ?: continue

                ParallaxBox.frame(e.renderAABB(), color, lineWidth)
            }
        }
    }
}
