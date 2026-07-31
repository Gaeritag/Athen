@file:Suppress("AssignedValueIsNeverRead", "VariableNeverRead")

package foo.starred.athen.hud

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.handlers.Chronos
import foo.starred.athen.handlers.Scribble
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.time.client

@Priority(-2)
object HUDManager {
    private val storage = Scribble("config/HUDEditor")
    val elements = mutableMapOf<String, HUDElement>()

    init {
        on<GuiEvent.Render.Main> {
            if (client.screen is HUDEditor) return@on
            if (client.options.hideGui && ModSettings.hideGuis) return@on

            Resolute.push(graphics)

            for (element in elements.values) {
                if (!element.render0) continue

                graphics.pose().pushMatrix()
                graphics.pose().translate(element.x, element.y)
                graphics.pose().scale(element.scale, element.scale)

                graphics.pose().pushMatrix()
                element.render(graphics, false)
                graphics.pose().popMatrix()

                graphics.pose().popMatrix()
            }

            Resolute.pop(graphics)
        }
    }

    fun register(element: HUDElement) {
        elements[element.id] = element
        Chronos.schedule(1.client){ get(element.id) }
    }

    fun set() {
        for ((id, element) in elements) {
            var x by storage.float("$id.x", element.defaultX)
            var y by storage.float("$id.y", element.defaultY)
            var scale by storage.float("$id.scale", element.defaultScale)

            x = element.x
            y = element.y
            scale = element.scale
        }

        storage.save()
    }

    fun get(id: String) {
        val element = elements[id] ?: return

        val x by storage.float("$id.x", element.defaultX)
        val y by storage.float("$id.y", element.defaultY)
        val scale by storage.float("$id.scale", element.defaultScale)

        element.x = x
        element.y = y
        element.scale = scale
    }
}
