package foo.starred.athen.handlers

import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.render.animations.easeInOutCubic
import foo.starred.athen.utils.render.animations.easeOutQuad
import foo.starred.athen.utils.render.animations.linear
import foo.starred.athen.utils.render.animations.timedValue
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.play
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min

@Priority
object Notifier {
    private val notifications = CopyOnWriteArrayList<Notification>()
    private val str0 by lazy { client.font?.width("✕") ?: 0 }

    @JvmStatic
    @JvmOverloads
    fun String.notify(
        header: String = "Notification",
        duration: Int = 3000,
        playSound: Boolean = true,
        soundType: SoundEvent = SoundEvents.NOTE_BLOCK_CHIME.value()
    ) {
        notifications.add(Notification(header.parse(), parse(), duration))
        if (playSound) soundType.play()
    }

    init {
        on<GuiEvent.Render.Post> {
            notifications.removeIf { it.eat() }
            if (notifications.isEmpty()) return@on
            render(graphics)
        }
    }

    private fun render(graphics: GuiGraphics) {
        val screenWidth = client.window.guiScaledWidth
        val screenHeight = client.window.guiScaledHeight
        var currentY = screenHeight - 6

        for (n in notifications.asReversed()) {
            val h = n.height()
            val y = currentY - h
            n.render(graphics, screenWidth, y)
            currentY = y - 4
        }
    }

    private class Notification(
        val header: Component,
        val message: Component,
        val duration: Int
    ) {
        private val slideIn = timedValue(1f, 300L, ::easeOutQuad)
        private val slideOut = timedValue(0f, 200L, ::easeInOutCubic)
        private val fadeOut = timedValue(1f, 200L, ::linear)

        private val start = System.currentTimeMillis()
        private var eating = false

        init {
            slideIn.value = 0f
        }

        fun height(): Int =
            34 + client.font.split(message, 184).size * client.font.lineHeight

        fun render(graphics: GuiGraphics, screenWidth: Int, y: Int) {
            val time = System.currentTimeMillis() - start
            if (time > duration && !eating) {
                eating = true
                slideOut.value = 1f
                fadeOut.value = 0f
            }

            val alpha = fadeOut.value
            val x = screenWidth - 206 + if (eating) (slideOut.value * 212).toInt() else (slideIn.value * 212).toInt()
            val h = height()

            val accent = Mocha.Mauve.withAlpha(alpha)
            graphics.rectangle(x, y, 200, 1, accent)
            graphics.rectangle(x, y + 1, 200, h, Mocha.Base.withAlpha(alpha * 0.95f))

            var cy = y + 9
            graphics.extractText(header, x + 8, cy, false, Mocha.Mauve.withAlpha(alpha))
            graphics.extractText("✕", x + 192 - str0, cy, false, Mocha.Subtext0.withAlpha(alpha))
            cy += 16

            val lines = client.font.split(message, 184)
            for (line in lines) {
                graphics.extractText(line, x + 8, cy, false, Mocha.Text.withAlpha(alpha))
                cy += client.font.lineHeight
            }

            val progress = min(1f, time.toFloat() / duration)
            if (!eating && progress < 1f) {
                val barW = (200 * progress).toInt()
                val barY = y + h - 2
                graphics.rectangle(x, barY, barW, 2, accent)
            }
        }

        fun eat(): Boolean {
            val slideOutDone = slideOut.value > 0.999f
            val fadeOutDone = fadeOut.value < 0.001f
            return eating && slideOutDone && fadeOutDone
        }
    }
}