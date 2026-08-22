package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.Category
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.modules.Module
import kotlin.time.Duration.Companion.minutes

@Load
object  LobbyMarker : Module(
    "Lobby marker",
    "Marks lobbies and alerts you if you have already been inside that lobby.",
    Category.GENERAL
) {
    private val removeAfter by config.slider("Remove after", 5, 0, 60, "minutes")
    private val onlyCrystalHollows by config.switch("Only in Crystal Hollows")
    private val kv: MutableMap<String, Long> = mutableMapOf()

    init {
        Scheduler.repeat(removeAfter.minutes) {
        }

        on<LocationEvent.Hypixel.Server> {
            if (type?.name != "SkyBlock") return@on
            if (mode != "crystal_hollows" && onlyCrystalHollows) return@on

            if (name !in kv) {
                kv[name] = System.currentTimeMillis()
                return@on
            }

            kv[name] = System.currentTimeMillis()
            "You've been in this lobby!".mod()
        }
    }
}