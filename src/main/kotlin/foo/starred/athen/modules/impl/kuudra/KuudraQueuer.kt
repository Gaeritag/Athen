package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks

@LoadableAthenModule
object KuudraQueuer : Module(
    "Kuudra queuer",
    "Automatically re-queues at the end of each run.",
    ConfigCategory.KUUDRA
) {
    private val delay by config.slider("Delay", 20, 0, 100, "ticks")

    private val partyRegex = Regex("^Party > (?:\\[[^]]*?] )?\\w{1,16}(?: [ቾ⚒])?: ?(?<message>.+)$")
    private var bool: Boolean = false

    init {
        on<MessageEvent.Chat.Receive> {
            val (message) = partyRegex.find(stripped)?.destructured ?: return@on
            if (message == "!dt") bool = true
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<KuudraEvent.End.Success> {
            if (bool) return@on ::bool.set(false)

            Scheduler.schedule(delay.clientTicks) {
                MinecraftCommands.server("instancerequeue")
            }
        }
    }
}
