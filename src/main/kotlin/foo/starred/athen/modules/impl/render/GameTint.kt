package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.modules.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.data.Observable.Companion.and
import net.minecraft.client.gui.GuiGraphicsExtractor

@LoadableAthenModule
object GameTint : Module(
    "Game tint",
    "Tints the game screen in the color of your choice!",
    ConfigCategory.RENDER
) {
    private val color by config.colorPicker("Tint color", 0x19000000)
    private val last = config.switch("Tint HUDs", true).unique("hudTint")
    private val gui = config.switch("Tint GUIs", true).unique("screenTint")

    private val _state = Observable(false)
    private val state = _state.and(gui.state).map { !(it) }

    init {
        on<GuiEvent.Open.Any> {
            _state.value = true
        }

        on<GuiEvent.Close.Any> {
            _state.value = false
        }

        on<GuiEvent.Render.Any.Pre>(-100) {
            graphics.tint()
        }.runWhen(state and last.state.map { !it })

        on<GuiEvent.Render.Any.Post>(-100) {
            graphics.tint()
        }.runWhen(state and last.state)

        on<GuiEvent.Render.Screen.Post> {
            graphics.tint()
        }.runWhen(_state and gui.state)
    }

    private fun GuiGraphicsExtractor.tint() {
        fill(0, 0, guiWidth(), guiHeight(), color)
    }
}
