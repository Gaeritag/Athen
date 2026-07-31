package foo.starred.athen.modules.impl.slayer

import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroups
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.Category
import foo.starred.athen.ducks.entity.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.utils.literal

@Load
@OnlyIn(islands = [SkyBlockIsland.CRIMSON_ISLE])
object AttunementDisplay : Module(
    "Attunement display",
    "Displays the current attunement for blaze slayer, does not work with demons.",
    Category.SLAYER
) {
    private val regex = Regex("^(?<attunement>[A-Z]+) ♨(?<count>\\d) \\d\\d:\\d\\d$")

    private val count by config.switch("Display count")

    private val ex0 = "§l§eAURIC ♨5".fcs
    private var last: FormattedCharSequence? = null

    init {
        config.hud("Attunement display") {
            if (it) return@hud sizedText(ex0)
            return@hud sizedText(last ?: return@hud null)
        }

        on<EntityEvent.Update.Named> {
            val e = entity.parent ?: return@on
            val s = SlayerAPI.bosses[e] ?: return@on
            if (!s.owned) return@on

            val a = regex.findGroups(stripped, "attunement", "count") ?: return@on
            val b = a["attunement"]?.fn() ?: return@on
            val c = a["count"]?.toIntOrNull() ?: return@on

            last = b.fn0(c).literal().visualOrderText
        }

        on<SlayerEvent.Boss.Death> {
            last = null
        }

        on<SlayerEvent.Reset.Any> {
            last = null
        }
    }

    private fun String.fn(): String? = when (this) {
        "ASHEN" -> "§l§8$this"
        "AURIC" -> "§l§e$this"
        "CRYSTAL" -> "§l§b$this"
        "SPIRIT" -> "§l§f$this"
        else -> null
    }

    private fun String.fn0(int: Int): String {
        if (!count) return this
        return "$this ♨$int"
    }
}