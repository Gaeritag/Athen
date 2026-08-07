@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.config.ui

import foo.starred.athen.Athen
import foo.starred.athen.annotations.Priority
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.ConfigManager.updateConfig
import foo.starred.athen.config.ui.elements.FeatureTooltip
import foo.starred.athen.config.ui.elements.HelpTooltip
import foo.starred.athen.config.ui.panels.Panel
import foo.starred.athen.handlers.Scram
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.nvg.NVGRenderer
import foo.starred.athen.utils.nvg.NVGSpecialRenderer
import foo.starred.athen.utils.render.animations.easeOutQuad
import foo.starred.athen.utils.render.animations.timedValue
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.shift
import foo.starred.snowbird.utils.hovered
import foo.starred.snowbird.utils.mouseRX
import foo.starred.snowbird.utils.mouseRY
import foo.starred.snowbird.utils.open
import net.minecraft.client.gui.GuiGraphics
import kotlin.math.sign

@Priority(-3)
object ClickGUI : Scram("Config [Click UI - Athen]") {
    private val panels = mutableListOf<Panel>()
    private val `anim$open` = timedValue(0f, 300L, ::easeOutQuad)
    private lateinit var searchBar: SearchBar

    lateinit var featureTooltip: FeatureTooltip
        private set

    private lateinit var helpTooltip: HelpTooltip

    override fun onScramInit() {
        panels.clear()

        ConfigManager.features.entries
            .sortedBy { it.key.ordinal }
            .forEachIndexed { index, (category, features) ->
                val col = index % 7
                val row = index / 7
                val x = if (row > 0) 50f + (col + 1) * 260f else 50f + col * 260f
                val y = 50f + row * 400f

                panels.add(Panel(category, features, x, y, ::updateConfig))
            }

        searchBar = SearchBar { query -> panels.forEach { it.applySearchFilter(query) } }
        featureTooltip = FeatureTooltip()
        helpTooltip = HelpTooltip()
        helpTooltip.initialize(client.window.width)
        `anim$open`.value = 1f
        super.onScramInit()
    }

    override fun onScramClose() {
        ConfigManager.save(true)
        `anim$open`.value = 0f
        super.onScramClose()
    }

    override fun isPauseScreen() = false

    override fun onScramRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        NVGSpecialRenderer.draw(graphics, 0, 0, graphics.guiWidth(), graphics.guiHeight()) {
            val width = client.window.width
            val height = client.window.height
            val t = `anim$open`.value
            val scale = 0.1f + 0.9f * t

            NVGRenderer.push()
            NVGRenderer.globalAlpha(t)
            NVGRenderer.drawText("${Athen.modName} ${Athen.modVersion}", 4f, height - 18f, 16f, Mocha.Text.argb, NVGRenderer.defaultFont)
            NVGRenderer.pop()

            NVGRenderer.push()
            NVGRenderer.translate(width / 2f, height / 2f)
            NVGRenderer.scale(scale, scale)
            NVGRenderer.translate(-width / 2f, -height / 2f)
            NVGRenderer.globalAlpha(t)

            panels.forEach { it.draw(mouseRX, mouseRY) }
            searchBar.draw(width / 2f - 175f, height - 110f, mouseRX, mouseRY)
            drawDiscordButton(width / 2f - 55f, height - 60f)
            featureTooltip.draw(mouseRX, mouseRY)
            helpTooltip.draw(mouseRX, mouseRY)

            NVGRenderer.pop()
        }
    }

    private fun drawDiscordButton(x: Float, y: Float) {
        val buttonWidth = 110f
        val buttonHeight = 40f

        NVGRenderer.drawDropShadow(x, y, buttonWidth, buttonHeight, 10f, 0.75f, 9f)
        NVGRenderer.drawRectangle(x, y, buttonWidth, buttonHeight, Mocha.Base.argb, 9f)
        NVGRenderer.drawHollowRectangle(x, y, buttonWidth, buttonHeight, 3f, Mocha.Mauve.argb, 9f)

        val text = "Join Discord"
        val textX = x + buttonWidth / 2f - 44f
        val textY = y + (buttonHeight - 16f) / 2f

        NVGRenderer.drawText(text, textX, textY, 16f, Mocha.Text.argb, NVGRenderer.defaultFont)
    }

    override fun onScramMouseScroll(mouseX: Int, mouseY: Int, horizontal: Double, vertical: Double): Boolean {
        if (shift) {
            val scroll = vertical.toFloat() * 20f
            val leftmost = panels.minOfOrNull { it.x } ?: 0f
            val rightmost = panels.maxOfOrNull { it.x + 240f } ?: 0f

            if ((scroll > 0 && leftmost < 50f) || (scroll < 0 && rightmost > client.window.width - 50f)) {
                panels.forEach { it.x += scroll }
            }

            return true
        }

        val amount = (vertical.sign * 16).toInt()
        return panels.reversed().any { it.handleScroll(amount) } || super.onScramMouseScroll(mouseX, mouseY, horizontal, vertical)
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) {
            val width = client.window.width
            val height = client.window.height

            if (hovered(width / 2f - 55f, height - 60f, 110f, 40f)) {
                Athen.discordUrl.open()
                return true
            }
        }

        if (helpTooltip.mouseClicked(mouseRX, mouseRY, button)) return true
        searchBar.mouseClicked(mouseRX, mouseRY, button)
        return panels.reversed().any { it.mouseClicked(mouseRX, mouseRY, button) } || super.onScramMouseClick(mouseX, mouseY, button)
    }

    override fun onScramMouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        helpTooltip.mouseReleased(button)
        searchBar.mouseReleased()
        panels.forEach { it.mouseReleased(button) }
        return super.onScramMouseRelease(mouseX, mouseY, button)
    }

    override fun onScramCharType(char: Char): Boolean {
        searchBar.keyTyped(char)
        return panels.reversed().any { it.keyTyped(char) } || super.onScramCharType(char)
    }

    override fun onScramKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        searchBar.keyPressed(keyCode)
        return panels.reversed().any { it.keyPressed(keyCode, scanCode) } || super.onScramKeyPress(keyCode, scanCode, modifiers)
    }
}