package foo.starred.athen.config.hud.impl

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.hud.data.element.HudElement
import foo.starred.athen.config.hud.ui.HudElementEditorUI
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.cascade.graphics.geometry.CascadeGeometricResolution
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.gui.MinecraftGui

@LoadableAthenModule
object HudRenderer {
    val resolution = CascadeGeometricResolution.FHD.of(2f)
    val elements = mutableMapOf<String, HudElement>()

    var scale = 1f
        private set

    var width = 960f
        private set

    var height = 540f
        private set

    init {
        on<GuiEvent.Render.Any.Main> {
            if (MinecraftGui.screen is HudElementEditorUI) return@on
            if (MinecraftGui.hidden && ModSettings.hideGuis) return@on

            graphics.pose().pushMatrix()
            graphics.pose().scale(scale)

            for (element in elements.values) {
                if (!element.render1) continue

                graphics.pose().pushMatrix()
                graphics.pose().translate(element.coordinate.x, element.coordinate.y)
                graphics.pose().scale(element.coordinate.scale)

                graphics.pose().pushMatrix()
                element.render(graphics)
                graphics.pose().popMatrix()

                graphics.pose().popMatrix()
            }

            graphics.pose().popMatrix()
        }
    }

    fun constrain() {
        for ((_, element) in elements) {
            element.config.constrain()
        }
    }

    fun compute() {
        CascadeGeometricResolution.compute(resolution, client.window.guiScaledWidth, client.window.guiScaledHeight) { scale1, width1, height1 ->
            scale = scale1
            width = width1
            height = height1
        }
    }

    fun save() {
        for ((id, element) in elements) {
            ConfigManager.values["$id.x"] = element.coordinate.x
            ConfigManager.values["$id.y"] = element.coordinate.y
            ConfigManager.values["$id.scale"] = element.coordinate.scale
        }

        ConfigManager.save(true)
    }
}
