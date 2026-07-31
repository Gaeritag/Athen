package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.config.Category
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.player

@Load
@OnlyIn(skyblock = true)
object LagDetector : Module(
    "Lag detector",
    "Displays a timer since the last server tick if it was older than the threshold.",
    Category.GENERAL
) {
    private val threshold by config.slider("Threshold", 750, 100, 5000, "ms")
    private var lastTick = 0L

    private val ex0 = "§c67ms".fcs

    init {
        config.hud("Lag display") {
            if (it) return@hud sizedText(ex0)
            if (lastTick == 0L) return@hud null
            if (player == null) return@hud null

            val t = System.currentTimeMillis() - lastTick
            if (t <= threshold) return@hud null

            sizedText("§c${t}ms")
        }

        on<LocationEvent.Server.Connect> {
            lastTick = 0
        }

        on<TickEvent.Server> {
            lastTick = System.currentTimeMillis()
        }
    }
}