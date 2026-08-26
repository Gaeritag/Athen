package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.Category
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.command
import foo.starred.snowbird.api.name
import foo.starred.snowbird.api.scheduling.scheduler.extensions.clientTicks
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull

@Load
object KuudraQueuer : Module(
    "Kuudra queuer",
    "Automatically re-queues at the end of each run.",
    Category.KUUDRA
) {
    private val delay by config.slider("Delay", 20, 0, 100, "ticks")

    private val partyRegex = Regex("^Party > (?:\\[[^]]*?] )?\\w{1,16}(?: [ቾ⚒])?: ?(?<message>.+)$")
    private var bool: Boolean = false

    init {
        on<MessageEvent.Chat.Receive> {
            if (PartyAPI.leader?.name?.equals(name) ?: false) return@on

            partyRegex.findOrNull(stripped, "message") { (message) ->
                if (message == "!dt") bool = true
            }
        }.runWhen(SkyBlockIsland.KUUDRA.inIsland)

        on<KuudraEvent.End.Success> {
            if (PartyAPI.leader?.name?.equals(name) ?: false) return@on
            if (bool) return@on ::bool.set(false)

            Scheduler.schedule(delay.clientTicks) {
                "instancerequeue".command()
            }
        }
    }
}
