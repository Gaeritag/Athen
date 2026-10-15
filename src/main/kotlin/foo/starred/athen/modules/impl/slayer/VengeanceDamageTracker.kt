package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.modules.Module
import foo.starred.snowbird.utils.abbreviate
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup

@Load
@OnlyIn(islands = [PresetSkyBlockIsland.CRIMSON_ISLE])
object VengeanceDamageTracker : Module(
    "Vengeance damage tracker",
    "Tracks your vengeance damage in chat.",
    ConfigCategory.SLAYER
) {
    private val abbreviate by config.switch("Abbreviate damage")
    private val regex: Regex = Regex("""^(?<damage>\d+(?:,\d+)*)ﬗ$""")

    init {
        on<EntityEvent.Update.Named> {
            val slayer = SlayerAPI.slayer?.entity ?: return@on
            val match = regex.findGroup(stripped, "damage") ?: return@on

            if (entity.distanceTo(slayer) > 5) return@on

            val damage = match.replace(",", "").toLong()
            if (damage < 500_000) return@on

            val displayDamage = if (abbreviate) damage.abbreviate() else match
            "Vengeance -> <${MochaColorScheme.Red.argb}>$displayDamage".mod()
        }
    }
}
