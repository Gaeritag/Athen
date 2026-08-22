package foo.starred.athen.updater

import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.api.screen.MultiVersionScreen
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import net.minecraft.client.gui.GuiGraphicsExtractor

class UpdateGUI(
    private val currentVersion: String,
    private val newVersion: String,
    private val onUpdate: () -> Unit,
    private val onSkip: () -> Unit,
    private val onRemind: () -> Unit
) : MultiVersionScreen("Update GUI [Athen]") {
    private var booling = false

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onScramRender(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        graphics.rectangle(0, 0, width, height, Mocha.Crust.withAlpha(0.6f))
        graphics.drawPanel((width - 360) / 2, (height - 175) / 2)
    }

    private fun GuiGraphicsExtractor.drawPanel(px: Int, py: Int) {
        rectangle(px, py, 360, 28, Mocha.Base.argb)
        rectangle(px, py + 28, 360, 175 - 28, Mocha.Mantle.argb)
        outline(px, py, 360, 175, 1, Mocha.Surface0.argb)
        rectangle(px, py + 28, 360, 1, Mocha.Surface0.argb)

        extractText("Update available for Athen", px + 16, py + 10, false, Mocha.Lavender.argb)

        val a = client.font.lineHeight + 6
        val b = py + 40

        extractText("Current version:", px + 16, b, false, Mocha.Subtext0.argb)
        extractText(currentVersion, px + 344 - client.font.width(currentVersion), b, false, Mocha.Text.argb)

        extractText("New version:", px + 16, b + a, false, Mocha.Subtext0.argb)
        extractText(newVersion, px + 344 - client.font.width(newVersion), b + a, false, Mocha.Green.argb)

        rectangle(px + 16, b + a + 30, 330, 1, Mocha.Surface0.argb)

        drawButton(px + 16, py + 175 - 34, "Update Now",   Mocha.Green.argb)
        drawButton(px + 128, py + 175 - 34, "Remind Later", Mocha.Peach.argb)
        drawButton(px + 240, py + 175 - 34, if (booling) "Confirm?" else "Skip Version", Mocha.Red.argb)
    }

    private fun GuiGraphicsExtractor.drawButton(x: Int, y: Int, label: String, color: Int) {
        val b = hovered(x, y, 104, 22, true)
        rectangle(x, y, 104, 22, if (b) color else Mocha.Surface1.argb)
        outline(x, y, 104, 22, 1, color)
        extractText(label, x + (104 - client.font.width(label)) / 2, y + (22 - client.font.lineHeight) / 2 + 1, false, if (b) Mocha.Base.argb else color)
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button != 0) return super.onScramMouseClick(mouseX, mouseY, button)

        val x = (width - 360) / 2 + 16
        val y = (height - 175) / 2 + 141

        fun fn(i: Int): Boolean {
            val xo = x + i * (104 + 8)
            return hovered(xo, y, 104, 22, true)
        }

        when {
            fn(0) -> {
                onUpdate()
                //~ if >= 26.2 'client.setScreen' -> 'client.gui.setScreen'
                client.setScreen(null)
            }

            fn(1) -> {
                if (booling) {
                    booling = false
                    return true
                }

                onRemind()
                "Will remind to update for version $newVersion on next launch".mod()
                //~ if >= 26.2 'client.setScreen' -> 'client.gui.setScreen'
                client.setScreen(null)
            }

            fn(2) -> {
                if (booling) {
                    onSkip()
                    "Skipped update for version $newVersion".mod()
                    //~ if >= 26.2 'client.setScreen' -> 'client.gui.setScreen'
                    client.setScreen(null)
                    return true
                }

                booling = true
            }

            else -> return super.onScramMouseClick(mouseX, mouseY, button)
        }

        return true
    }
}