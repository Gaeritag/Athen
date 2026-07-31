package foo.starred.athen.api.rendering.ui.text.vanilla.extensions

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import foo.starred.snowbird.api.client

//~ if >= 26.1 'drawString(' -> 'text(' {
@JvmOverloads
@JvmName("text_string")
fun GuiGraphics.extractText(text: String, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
    val xx = if (center) x - client.font.width(text) / 2 else x
    drawString(client.font, text, xx, y, color, shadow)
}

@JvmOverloads
@JvmName("text_component")
fun GuiGraphics.extractText(text: Component, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
    val xx = if (center) x - client.font.width(text) / 2 else x
    drawString(client.font, text, xx, y, color, shadow)
}

@JvmOverloads
@JvmName("text_fcs")
fun GuiGraphics.extractText(text: FormattedCharSequence, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
    val xx = if (center) x - client.font.width(text) / 2 else x
    drawString(client.font, text, xx, y, color, shadow)
}

@JvmOverloads
@JvmName("text_string_multi")
fun GuiGraphics.extractText(texts: List<String>, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
    extractText(texts.map { Language.getInstance().getVisualOrder(FormattedText.of(it)) }, x, y, shadow, color, spacing, center)
}

@JvmOverloads
@JvmName("text_component_multi")
fun GuiGraphics.extractText(texts: List<Component>, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
    extractText(texts.map { it.visualOrderText }, x, y, shadow, color, spacing, center)
}

@JvmOverloads
@JvmName("text_fcs_multi")
fun GuiGraphics.extractText(texts: List<FormattedCharSequence>, x: Int, y: Int, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
    val widths = texts.map { client.font.width(it) }
    drawTexts(texts, widths, x, y, color, shadow, spacing, center)
}

@JvmOverloads
@JvmName("sizedText_string")
fun GuiGraphics.sizedText(text: String, shadow: Boolean = true, color: Int = -1, center: Boolean = false): Pair<Int, Int> {
    extractText(text, 0, 0, shadow, color, center)
    return client.font.width(text) to client.font.lineHeight
}

@JvmOverloads
@JvmName("sizedText_component")
fun GuiGraphics.sizedText(text: Component, shadow: Boolean = true, color: Int = -1, center: Boolean = false): Pair<Int, Int> {
    extractText(text, 0, 0, shadow, color, center)
    return client.font.width(text) to client.font.lineHeight
}

@JvmOverloads
@JvmName("sizedText_fcs")
fun GuiGraphics.sizedText(text: FormattedCharSequence, shadow: Boolean = true, color: Int = -1, centered: Boolean = false): Pair<Int, Int> {
    extractText(text, 0, 0, shadow, color, centered)
    return client.font.width(text) to client.font.lineHeight
}

@JvmOverloads
@JvmName("sizedText_string_multi")
fun GuiGraphics.sizedText(texts: List<String>, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()): Pair<Int, Int> {
    return sizedText(texts.map { Language.getInstance().getVisualOrder(FormattedText.of(it)) }, shadow, color, spacing, center)
}

@JvmOverloads
@JvmName("sizedText_component_multi")
fun GuiGraphics.sizedText(texts: List<Component>, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()): Pair<Int, Int> {
    return sizedText(texts.map { it.visualOrderText }, shadow, color, spacing, center)
}

@JvmOverloads
@JvmName("sizedText_fcs_multi")
fun GuiGraphics.sizedText(texts: List<FormattedCharSequence>, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()): Pair<Int, Int> {
    val widths = texts.map { client.font.width(it) }

    drawTexts(texts, widths, 0, 0, color, shadow, spacing, center)

    val h = texts.size * client.font.lineHeight + (texts.size - 1) * spacing
    return (widths.maxOrNull() ?: 0) to h
}

private fun GuiGraphics.drawTexts(lines: List<FormattedCharSequence>, widths: List<Int>, x: Int, y: Int, color: Int, shadow: Boolean, spacing: Int, center: List<Int>) {
    val max = widths.maxOrNull() ?: 0
    for (i in lines.indices) {
        val xx = if (i in center) x + (max - widths[i]) / 2 else x
        val yy = y + i * (client.font.lineHeight + spacing)
        drawString(client.font, lines[i], xx, yy, color, shadow)
    }
}
//~ }