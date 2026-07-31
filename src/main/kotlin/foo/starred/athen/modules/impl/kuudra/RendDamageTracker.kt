package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraTier
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.config.Category
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.abbreviate
import foo.starred.snowbird.utils.toDurationFromMillis

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object RendDamageTracker : Module(
    "Rend damage tracker",
    "Tries to detect how much damage someone did.",
    Category.KUUDRA
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
            val duration = KuudraPhase.Kill.durTime.toDurationFromMillis(secondsOnly = true, secondsDecimals = 1)

            "Detected <red>$damage<r> damage at <yellow>$duration<r>!".parse().modMessage()
        }
    }
}