@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.dungeon.terminals

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.dungeon.terminals.TerminalAPI
import foo.starred.athen.config.Category
import foo.starred.athen.events.DungeonEvent
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.handlers.Chronos
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.athen.utils.nvg.NVGRenderer
import foo.starred.athen.utils.nvg.NVGSpecialRenderer
import foo.starred.athen.utils.render.animations.springValue
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.Observable
import foo.starred.snowbird.utils.mouseRX
import foo.starred.snowbird.utils.mouseRY
import java.awt.Color
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Load
object TerminalClick : Module(
    "Terminal click",
    "Lines between when you clicked the mouse button in terminals",
    Category.DUNGEONS
) {
    private data class Click(val x: Float, val y: Float, val button: Int)
    private val clicks = mutableListOf<Click>()
    private var render = Observable(true)
    private var opacity = springValue(0f, 0.15f)

    private val radius by config.slider("Radius", 4, 1, 10)
    private val thickness by config.slider("Thickness", 2, 1, 10)
    private val `color$mouse$left` by config.colorPicker("Left mouse color", Color(Catppuccin.Mocha.Mauve.argb, true))
    private val `color$mouse$right` by config.colorPicker("Right mouse color", Color(Catppuccin.Mocha.Peach.argb, true))

    init {
        on<GuiEvent.Input.Mouse.Press> {
            clicks.add(Click(mouseRX, mouseRY, keyEvent.button()))
        }.runWhen(TerminalAPI.opened)

        on<GuiEvent.Render.Post> {
            val cs = clicks.toList()
            if (cs.isEmpty()) return@on

            val alpha = opacity.value
            if (alpha == 0f) return@on

            NVGSpecialRenderer.draw(graphics, 0, 0, client.window.width, client.window.height) {
                NVGRenderer.push()
                NVGRenderer.globalAlpha(alpha)

                for (i in 0 until cs.size - 1) {
                    val c1 = cs[i]
                    val c2 = cs[i + 1]
                    val color = if (c1.button == 0) `color$mouse$left`.rgb else `color$mouse$right`.rgb

                    NVGRenderer.drawLine(c1.x, c1.y, c2.x, c2.y, thickness.toFloat(), color)
                }

                for (c in cs) {
                    val color = if (c.button == 0) `color$mouse$left`.rgb else `color$mouse$right`.rgb
                    NVGRenderer.drawCircle(c.x, c.y, radius.toFloat(), color)
                }

                NVGRenderer.pop()
            }
        }.runWhen(render)

        on<DungeonEvent.Terminal.Close> {
            render.value = true

            Chronos.schedule(100.milliseconds) {
                opacity.value = 1f
            }

            Chronos.schedule(3.seconds) {
                opacity.value = 0f
            }

            Chronos.schedule(4.seconds) {
                reset()
            }
        }

        on<DungeonEvent.Terminal.Open> {
            reset()
            opacity.value = 0f
        }
    }

    private fun reset() {
        render.value = false
        clicks.clear()
    }
}