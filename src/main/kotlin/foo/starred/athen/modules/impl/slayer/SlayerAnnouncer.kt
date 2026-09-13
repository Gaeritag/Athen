package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.config.Category
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.command
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI

@Load
@OnlyIn(skyblock = true)
object SlayerAnnouncer : Module(
    "Slayer announcer",
    "Sends a message when your boss spawns",
    Category.SLAYER
) {
    private val party by config.switch("Check party status")
    private val slayer by config.switch("Check boss type")
    private val type by config.selector("Boss type", SlayerBoss.entries.map { it.short })
    private val tier by config.selector("Boss tier", SlayerTier.entries.map { it.name })

    init {
        on<SlayerEvent.Boss.Spawn> {
            if (!slayerInfo.owned) return@on
            if (party && !PartyAPI.inParty) return@on
            if (slayer && slayerInfo.type != SlayerBoss.entries.getOrNull(type)) return@on
            if (slayer && (slayerInfo.tier?.int ?: 0) < tier + 1) return@on

            val block = entity.blockPosition()
            "/pc ${slayerInfo.type?.display} T${slayerInfo.tier?.int} spawned at x: ${block.x}, y: ${block.y}, z: ${block.z}".command()
        }
    }
}
