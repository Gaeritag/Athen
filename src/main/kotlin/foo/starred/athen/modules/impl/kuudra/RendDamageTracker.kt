package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraTier
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.number.abbreviate
import foo.starred.kontext.api.format.extension.time.parseDuration

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object RendDamageTracker : Module(
    "Rend damage tracker",
    "Tries to detect how much damage someone did.",
    ConfigCategory.KUUDRA
) {
    init {
        on<EntityEvent.Update.Health> {
            if (!KuudraAPI.inRun) return@on
            if (KuudraAPI.tier != KuudraTier.INFERNAL) return@on
            if (KuudraAPI.phase != KuudraPhase.Kill) return@on
            if (entity != KuudraAPI.kuudra) return@on
            if (new > 25000) return@on

            val old = old ?: return@on
            val diff = (old - new).takeIf { it > 1666 } ?: return@on
            val player = client.player ?: return@on
            if (player.position().y > 30) return@on
            val damage = (diff * 9600).abbreviate()
            val duration = (KuudraPhase.Kill.durTime / 1000).parseDuration(short = true, decimals = 1)

            "Detected <red>$damage<r> damage at <yellow>$duration<r>!".mod()
        }
    }
}
