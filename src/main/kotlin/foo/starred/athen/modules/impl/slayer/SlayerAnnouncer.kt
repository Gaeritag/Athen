package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object SlayerAnnouncer : Module(
    "Slayer announcer",
    "Sends a message when your boss spawns",
    ConfigCategory.SLAYER
) {
    private val slayer by config.switch("Check boss type")
    private val type by config.selector("Boss type", SlayerBoss.entries.map { it.short })
    private val tier by config.selector("Boss tier", SlayerTier.entries.map { it.name })

    init {
        on<SlayerEvent.Boss.Spawn> {
            if (!slayerInfo.owned) return@on
            if (slayer && slayerInfo.type != SlayerBoss.entries.getOrNull(type)) return@on
            if (slayer && (slayerInfo.tier?.int ?: 0) < tier + 1) return@on

            val block = entity.blockPosition()
            MinecraftCommands.client("/pc ${slayerInfo.type?.display} T${slayerInfo.tier?.int} spawned at x: ${block.x}, y: ${block.y}, z: ${block.z}")
        }
    }
}
