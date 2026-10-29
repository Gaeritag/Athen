@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.ducks.entity.generic.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.format.extension.component.literal
import net.minecraft.util.FormattedCharSequence

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.CRIMSON_ISLE])
object AttunementDisplay : Module(
    "Attunement display",
    "Displays the current attunement for blaze slayer, does not work with demons.",
    ConfigCategory.SLAYER
) {
    private val count by config.switch("Display count")
    private val hud by config.hud("Attunement display") {
        val example = "§l§eAURIC ♨5".formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            MinecraftFontRenderer.extract(graphics, last ?: return@render, 0, 0)
        }
    }

    private val regex = Regex("^(?<attunement>[A-Z]+) ♨(?<count>\\d) \\d\\d:\\d\\d$")
    private var last: FormattedCharSequence? = null

    init {
        on<EntityEvent.Update.Named> {
            val e = entity.parent ?: return@on
            val s = SlayerAPI.bosses[e] ?: return@on
            if (!s.owned) return@on

            val (attunement, count) = regex.find(stripped)?.destructured ?: return@on
            val b = attunement.fn() ?: return@on
            val c = count.toIntOrNull() ?: return@on

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
