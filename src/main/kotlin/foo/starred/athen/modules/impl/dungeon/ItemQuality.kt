@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl.dungeon

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.quality
import foo.starred.athen.utils.tier
import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.network.chat.Component

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object ItemQuality : Module(
    "Item quality",
    "Shows the quality of dungeon items.",
    ConfigCategory.DUNGEONS
) {
    private val textStyle by config.input("Style", "&7Item Quality: &c#cur&8/&c#max &8(#floor)")
    private val _meow0 by config.variables("#cur", "#max", "#floor")

    init {
        on<GuiEvent.Tooltip.Update> {
            val cur = item.quality() ?: return@on
            val f = item.tier() ?: return@on

            tooltip.add(1, str(cur.toString(), f))
        }
    }

    private fun str(cur: String, f: Int): Component = textStyle
        .replace("&", "§")
        .replace("#cur", cur)
        .replace("#max", "50")
        .replace("#floor", "Floor $f")
        .parse()
}
