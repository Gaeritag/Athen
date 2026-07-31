package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module

@Load
object LobbyMarker : Module(
    "Lobby marker",
    "Marks lobbies and alerts you if you have already been inside that lobby.",
    Category.GENERAL
) {
    private val onlyCrystalHollows by config.switch("Only in Crystal Hollows")
    private val lobbies = mutableSetOf<String>()

    init {
        on<LocationEvent.Hypixel.Server> {
            if (type?.name != "SkyBlock") return@on
            if (mode != "crystal_hollows" && onlyCrystalHollows) return@on
            if (lobbies.add(name)) return@on

            "You've been in this lobby!".modMessage()
        }
    }
}