@file:Suppress("PrivatePropertyName")

package foo.starred.athen.config.ui.elements

import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.ui.elements.base.IBaseUI
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.nvg.NVGRenderer
import foo.starred.athen.utils.render.animations.easeOutQuad
import foo.starred.athen.utils.render.animations.springValue
import foo.starred.athen.utils.render.animations.timedValue
import foo.starred.snowbird.utils.hovered

class ExpandableElement(
    name: String,
    configKey: String,
    onUpdate: (String, Any) -> Unit
) : IBaseUI(name, configKey, onUpdate) {

    companion object {
        private val chevronIcon = NVGRenderer.createImage("/assets/athen/chevron.svg", Mocha.Text.argb)
    }

    private var expanded = ConfigManager.getValue(configKey) as? Boolean ?: false
    private val `anim$chevron` = timedValue(if (expanded) 90f else 0f, 200, ::easeOutQuad)
    private val `anim$accent` = springValue(if (expanded) Mocha.Mauve.argb else Mocha.Surface0.argb, 0.2f)
    private val `anim$text` = springValue(if (expanded) Mocha.Mauve.argb else Mocha.Subtext0.argb, 0.2f)

    override fun draw(x: Float, y: Float, mouseX: Float, mouseY: Float): Float {
        lastX = x
        lastY = y

        val rowX = x + 6f
        val rowY = y + 4f
        val rowW = width - 12f

        `anim$accent`.value = if (expanded) Mocha.Mauve.argb else Mocha.Surface0.argb
        `anim$chevron`.value = if (expanded) 90f else 0f
        `anim$text`.value = when {
            expanded -> Mocha.Mauve.argb
            hovered(rowX, rowY, rowW, 26f) -> Mocha.Text.argb
            else -> Mocha.Subtext0.argb
        }

        NVGRenderer.drawRectangle(rowX, rowY + 3f, 2f, 20f, `anim$accent`.value, 1f)

        drawText(name, rowX + 8f, rowY + 7f, 14f, `anim$text`.value)

        NVGRenderer.push()
        NVGRenderer.translate((rowX + rowW - 12f) + 5f, rowY + 13f)
        NVGRenderer.rotate(Math.toRadians(`anim$chevron`.value.toDouble()).toFloat())
        NVGRenderer.translate(-5f, -5f)
        NVGRenderer.drawImage(chevronIcon, 0f, 0f, 10f, 10f)
        NVGRenderer.pop()

        return 34f
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, button: Int): Boolean {
        if (button != 0) return false
        if (!hovered(lastX + 6f, lastY + 4f, width - 12f, 26f)) return false

        expanded = !expanded
        onUpdate(configKey, expanded)
        return true
    }

    override fun getHeight() = 34f
}