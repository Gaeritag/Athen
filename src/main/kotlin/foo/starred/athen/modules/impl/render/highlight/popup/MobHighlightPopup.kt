package foo.starred.athen.modules.impl.render.highlight.popup

import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.handlers.Scram
import foo.starred.athen.modules.impl.render.highlight.MobHighlight
import foo.starred.athen.modules.impl.render.highlight.popup.data.UIZoneType
import foo.starred.athen.ui.InputField
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import org.lwjgl.glfw.GLFW

object MobHighlightPopup : Scram("Add Highlight [Athen]") {
    private val colorField = InputField("Hex color (e.g. ff0000)")
    private val zones = mutableListOf<UIZone>()

    private var typed = false
    private var name0: String? = null
    private var type0: EntityType<*>? = null
    private var max0 = -1
    private var useMax = true

    fun open(name: String?, type: EntityType<*>, max: Int) {
        name0 = name
        type0 = type
        max0 = max
        typed = name == null
        useMax = true

        colorField.reset(true)
        colorField.value = "ff0000"
        colorField.cursor = 6
        colorField.focused = true

        open()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onScramRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        zones.clear()

        graphics.rectangle(0, 0, width, height, Catppuccin.Mocha.Crust.withAlpha(0.6f))

        val px = (width - 260) / 2
        val py = (height - 140) / 2

        graphics.rectangle(px, py, 260, 140, Catppuccin.Mocha.Base.argb)
        graphics.outline(px, py, 260, 140, 1, Catppuccin.Mocha.Surface0.argb)

        graphics.extractText("Add Highlight", px + 8, py + 7, false, Catppuccin.Mocha.Mauve.argb)
        graphics.rectangle(px, py + 22, 260, 1, Catppuccin.Mocha.Surface0.argb)

        var cy = py + 28

        graphics.rectangle(px + 8, cy, 120, 16, if (!typed && name0 != null) Catppuccin.Mocha.Surface1.argb else Catppuccin.Mocha.Mantle.argb)
        graphics.outline(px + 8, cy, 120, 16, 1, if (!typed && name0 != null) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Crust.argb)
        graphics.extractText("Named", px + 8 + (120 - client.font.width("Named")) / 2, cy + (16 - client.font.lineHeight) / 2 + 1, false, if (name0 == null) Catppuccin.Mocha.Overlay0.argb else if (!typed) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Subtext0.argb)
        if (name0 != null) zones.add(UIZone(px + 8, cy, 120, 16, UIZoneType.TOGGLE_NAMED))

        graphics.rectangle(px + 132, cy, 120, 16, if (typed) Catppuccin.Mocha.Surface1.argb else if (hovered(px + 132, cy, 120, 16, true)) Catppuccin.Mocha.Surface0.withAlpha(0.5f) else Catppuccin.Mocha.Mantle.argb)
        graphics.outline(px + 132, cy, 120, 16, 1, if (typed) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Crust.argb)
        graphics.extractText("Typed", px + 132 + (120 - client.font.width("Typed")) / 2, cy + (16 - client.font.lineHeight) / 2 + 1, false, if (typed) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Subtext0.argb)
        zones.add(UIZone(px + 132, cy, 120, 16, UIZoneType.TOGGLE_TYPED))

        cy += 22

        graphics.extractText("${if (typed) "Type" else "Name"}: ", px + 8, cy + 1, false, Catppuccin.Mocha.Subtext0.argb)
        graphics.enableScissor(px + 8 + client.font.width("${if (typed) "Type" else "Name"}: "), cy, px + 252, cy + client.font.lineHeight + 2)
        graphics.extractText(if (typed) type0?.let { BuiltInRegistries.ENTITY_TYPE.getKey(it).toString() } ?: "???" else name0 ?: "\u2014", px + 8 + client.font.width("${if (typed) "Type" else "Name"}: "), cy + 1, false, Catppuccin.Mocha.Text.argb)
        graphics.disableScissor()

        cy += client.font.lineHeight + 4

        graphics.rectangle(px + 8, cy, 10, 10, if (useMax) Catppuccin.Mocha.Surface1.argb else Catppuccin.Mocha.Surface0.argb)
        graphics.outline(px + 8, cy, 10, 10, 1, if (useMax) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Overlay0.argb)
        if (useMax) graphics.extractText("✔", px + 9 + (10 - client.font.width("✔")) / 2, cy + 1, false, Catppuccin.Mocha.Mauve.argb)
        graphics.extractText("Filter Max HP: ", px + 22, cy + 1, false, if (useMax) Catppuccin.Mocha.Subtext0.argb else Catppuccin.Mocha.Overlay0.argb)
        graphics.extractText(if (max0 == -1) "any" else max0.toString(), px + 22 + client.font.width("Filter Max HP: "), cy + 1, false, if (useMax) Catppuccin.Mocha.Peach.argb else Catppuccin.Mocha.Overlay0.argb)
        zones.add(UIZone(px + 8, cy, 10 + client.font.width("Filter Max HP: ") + client.font.width(if (max0 == -1) "any" else max0.toString()) + 14, 10, UIZoneType.TOGGLE_MAX_HP))

        cy += client.font.lineHeight + 6

        graphics.extractText("Color", px + 8, cy, false, Catppuccin.Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        colorField.value.removePrefix("#").toIntOrNull(16).let {
            if (it != null) {
                graphics.rectangle(px + 8, cy + 1, 14, 14, it or 0xFF000000.toInt())
                graphics.outline(px + 8, cy + 1, 14, 14, 1, Catppuccin.Mocha.Overlay0.argb)
            } else {
                graphics.rectangle(px + 8, cy + 1, 14, 14, Catppuccin.Mocha.Surface0.argb)
                graphics.outline(px + 8, cy + 1, 14, 14, 1, Catppuccin.Mocha.Overlay0.argb)
            }
        }

        colorField.draw(graphics, px + 26, cy, 226) { zx, zy, zw, zh -> zones.add(
            UIZone(zx, zy, zw, zh, UIZoneType.COLOR_FIELD)
        ) }

        graphics.rectangle(px, py + 112, 260, 1, Catppuccin.Mocha.Surface0.argb)

        graphics.rectangle(px + 8, py + 118, 118, 16, if (hovered(px + 8, py + 118, 118, 16, true)) Catppuccin.Mocha.Surface2.argb else Catppuccin.Mocha.Surface1.argb)
        graphics.outline(px + 8, py + 118, 118, 16, 1, Catppuccin.Mocha.Red.argb)
        graphics.extractText("Cancel", px + 8 + (118 - client.font.width("Cancel")) / 2, py + 118 + (16 - client.font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Red.argb)
        zones.add(UIZone(px + 8, py + 118, 118, 16, UIZoneType.CANCEL))

        graphics.rectangle(px + 134, py + 118, 118, 16, if (hovered(px + 134, py + 118, 118, 16, true)) Catppuccin.Mocha.Surface2.argb else Catppuccin.Mocha.Surface1.argb)
        graphics.outline(px + 134, py + 118, 118, 16, 1, Catppuccin.Mocha.Green.argb)
        graphics.extractText("Save", px + 134 + (118 - client.font.width("Save")) / 2, py + 118 + (16 - client.font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Green.argb)
        zones.add(UIZone(px + 134, py + 118, 118, 16, UIZoneType.SAVE))
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button != 0) return false

        val hit = zones.lastOrNull { hovered(it.x, it.y, it.w, it.h, true) } ?: return true
        when (hit.type) {
            UIZoneType.TOGGLE_NAMED -> {
                typed = false
            }

            UIZoneType.TOGGLE_TYPED -> {
                typed = true
            }

            UIZoneType.COLOR_FIELD -> {
                if (colorField.focused) colorField.updateClick(mouseX, hit.x)
                colorField.focused = true
            }

            UIZoneType.TOGGLE_MAX_HP -> {
                useMax = !useMax
            }

            UIZoneType.SAVE -> {
                save()
            }

            UIZoneType.CANCEL -> {
                onClose()
            }
        }

        return true
    }

    override fun onScramKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (colorField.focused && colorField.handleKey(keyCode, modifiers)) return true

        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> onClose()
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> save()
        }

        return true
    }

    override fun onScramCharType(char: Char): Boolean {
        if (colorField.focused && (char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F')) colorField.handleChar(char)
        return true
    }

    private fun save() {
        val color = colorField.value.removePrefix("#").toIntOrNull(16) ?: return

        val hp = if (useMax) max0 else -1

        if (typed) {
            val type = type0 ?: return
            MobHighlight.e1.update { add(MobHighlight.EntityTyped(type, color, hp)) }
        } else {
            val name = name0 ?: return
            MobHighlight.e0.update { add(MobHighlight.EntityNamed(name, color, hp)) }
        }

        MobHighlight.scribble.save()
        onClose()
    }
}