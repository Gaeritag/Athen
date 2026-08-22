@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl.dungeon

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.config.Category
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.modules.Module
import foo.starred.snowbird.handlers.parser.parse
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData

@Load
@OnlyIn(skyblock = true)
object ItemQuality : Module(
    "Item quality",
    "Shows the quality of dungeon items.",
    Category.DUNGEONS
) {
    private val textStyle by config.input("Style", "&7Item Quality: &c#cur&8/&c#max &8(#floor)")
    private val _meow0 by config.variables("#cur", "#max", "#floor")

    init {
        on<GuiEvent.Tooltip.Update> {
            val cur = item.getData(DataTypes.DUNGEON_QUALITY) ?: return@on
            val f = item.getData(DataTypes.DUNGEON_TIER) ?: return@on

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