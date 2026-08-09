@file:Suppress("SameParameterValue")

package foo.starred.athen.modules.impl.render.highlight.ui

import foo.starred.athen.api.rendering.ui.effects.outline.outline
import foo.starred.athen.api.rendering.ui.shapes.rectangle.rectangle
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.extractText
import foo.starred.athen.api.screen.MultiVersionScreen
import foo.starred.athen.modules.impl.render.highlight.MobHighlight
import foo.starred.athen.modules.impl.render.highlight.ui.data.HighlightEntry
import foo.starred.athen.modules.impl.render.highlight.ui.data.UIZoneType
import foo.starred.athen.modules.impl.render.highlight.ui.renderers.ListRenderer
import foo.starred.athen.modules.impl.render.highlight.ui.renderers.ModalRenderer
import foo.starred.athen.ui.UIZone
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.snowbird.api.client
import foo.starred.snowbird.utils.hovered
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import org.lwjgl.glfw.GLFW

object MobHighlightGUI : MultiVersionScreen("Mob Highlights [Athen]") {
    private val entries = mutableListOf<HighlightEntry>()
    private val zones = mutableListOf<UIZone>()
    private val listRenderer = ListRenderer(28, 4, 16, 6)
    private val modal = ModalRenderer(340, 200, 16, 6)
    private var tab = false

    override fun onScramInit() {
        recreate()
        modal.close()
        tab = false
    }

    override fun onScramClose() = MobHighlight.json.save()

    override fun isPauseScreen() = false

    override fun onScramRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        zones.clear()
        graphics.rectangle(0, 0, width, height, Catppuccin.Mocha.Crust.withAlpha(0.6f))

        val pw = 460
        val ph = 300
        val px = (width - pw) / 2
        val py = (height - ph) / 2

        graphics.rectangle(px, py, pw, ph, Catppuccin.Mocha.Base.argb)
        graphics.outline(px, py, pw, ph, 1, Catppuccin.Mocha.Surface0.argb)

        tabs(graphics, px, py, pw)

        val ly = py + 34
        val lh = ph - 34 - 28
        val list = entries.filter { it.typed == tab }
        listRenderer.draw(graphics, mouseX, mouseY, px + 6, ly, pw - 12, lh, list, modal.open, zones)

        footer(graphics, px, py, pw, ph)
        if (modal.open) modal.draw(graphics, mouseX, mouseY, width, height, zones)
    }

    private fun tabs(graphics: GuiGraphics, px: Int, py: Int, pw: Int) {
        val tw = (pw - 3 * 2 - 4) / 2
        val th = 22
        val ty = py + 3

        val x0 = px + 3
        val b0 = !modal.open && hovered(x0, ty, tw, th, true)
        graphics.rectangle(x0, ty, tw, th, if (!tab) Catppuccin.Mocha.Surface1.argb else if (b0) Catppuccin.Mocha.Surface0.withAlpha(0.5f) else Catppuccin.Mocha.Mantle.argb)
        graphics.outline(x0, ty, tw, th, 1, if (!tab) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Crust.argb)
        graphics.extractText("Named", x0 + (tw - client.font.width("Named")) / 2, ty + (th - client.font.lineHeight) / 2 + 1, false, if (!tab) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Subtext0.argb)
        zones.add(UIZone(x0, ty, tw, th, UIZoneType.TAB_NAMED))

        val x1 = x0 + tw + 4
        val b1 = !modal.open && hovered(x1, ty, tw, th, true)
        graphics.rectangle(x1, ty, tw, th, if (tab) Catppuccin.Mocha.Surface1.argb else if (b1) Catppuccin.Mocha.Surface0.withAlpha(0.5f) else Catppuccin.Mocha.Mantle.argb)
        graphics.outline(x1, ty, tw, th, 1, if (tab) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Crust.argb)
        graphics.extractText("Typed", x1 + (tw - client.font.width("Typed")) / 2, ty + (th - client.font.lineHeight) / 2 + 1, false, if (tab) Catppuccin.Mocha.Mauve.argb else Catppuccin.Mocha.Subtext0.argb)
        zones.add(UIZone(x1, ty, tw, th, UIZoneType.TAB_TYPED))

        graphics.rectangle(px, py + 29, pw, 1, Catppuccin.Mocha.Surface0.argb)
    }

    private fun footer(graphics: GuiGraphics, px: Int, py: Int, pw: Int, ph: Int) {
        val fy = py + ph - 28
        graphics.rectangle(px, fy, pw, 1, Catppuccin.Mocha.Surface0.argb)

        val label = if (tab) "+ Add Typed" else "+ Add Named"
        val bw = 120
        val bx = px + (pw - bw) / 2
        val by = fy + 6
        val hov = !modal.open && hovered(bx, by, bw, 16, true)
        graphics.rectangle(bx, by, bw, 16, if (hov) Catppuccin.Mocha.Surface2.argb else Catppuccin.Mocha.Surface1.argb)
        graphics.outline(bx, by, bw, 16, 1, Catppuccin.Mocha.Green.argb)
        graphics.extractText(label, bx + (bw - client.font.width(label)) / 2, by + (16 - client.font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Green.argb)
        zones.add(UIZone(bx, by, bw, 16, UIZoneType.BUTTON_CREATE))
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (modal.open) {
            clickModal(mouseX, mouseY, button)
            return true
        }

        if (button != 0) return false

        val hit = zones.lastOrNull { hovered(it.x, it.y, it.w, it.h, true) } ?: return false

        if (hit.type == UIZoneType.TAB_NAMED) {
            tab = false
            return true
        }

        if (hit.type == UIZoneType.TAB_TYPED) {
            tab = true
            return true
        }

        if (hit.type == UIZoneType.BUTTON_CREATE) {
            modal.open(tab)
            return true
        }

        if (hit.type == UIZoneType.ENTRY_EDIT) {
            entries.firstOrNull { it.index == hit.data && it.typed == tab }?.let { modal.open(it) }
            return true
        }

        if (hit.type == UIZoneType.ENTRY_DELETE) {
            val e = entries.firstOrNull { it.index == hit.data && it.typed == tab } ?: return true
            if (e.typed) MobHighlight.e1.update { removeAt(e.index) }
            else MobHighlight.e0.update { removeAt(e.index) }
            recreate()
            return true
        }

        return true
    }

    private fun clickModal(mouseX: Int, mouseY: Int, button: Int) {
        if (button != 0) return

        val hit = zones.lastOrNull { hovered(it.x, it.y, it.w, it.h, true) }

        if (hit != null && hit.type == UIZoneType.MODAL_SUGGESTION) {
            modal.click(mouseX, mouseY)
            return
        }

        val p0 = modal.nameField.focused
        val p1 = modal.colorField.focused
        val p2 = modal.maxHpField.focused
        modal.nameField.focused = false
        modal.colorField.focused = false
        modal.maxHpField.focused = false

        if (hit == null) return

        if (hit.type == UIZoneType.MODAL_NAME || hit.type == UIZoneType.MODAL_TYPE) {
            modal.nameField.focused = true
            if (p0) modal.nameField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_COLOR) {
            modal.colorField.focused = true
            if (p1) modal.colorField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_MAX_HP) {
            modal.maxHpField.focused = true
            if (p2) modal.maxHpField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_SAVE) {
            save()
            return
        }

        if (hit.type == UIZoneType.MODAL_CANCEL) {
            modal.close()
            return
        }
    }

    override fun onScramKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (modal.open) {
            when {
                modal.nameField.focused && modal.nameField.handleKey(keyCode, modifiers) -> return true
                modal.colorField.focused && modal.colorField.handleKey(keyCode, modifiers) -> return true
                modal.maxHpField.focused && modal.maxHpField.handleKey(keyCode, modifiers) -> return true
            }

            when (keyCode) {
                GLFW.GLFW_KEY_TAB -> {
                    when {
                        modal.nameField.focused -> {
                            modal.nameField.focused = false
                            modal.colorField.focused = true
                        }

                        modal.colorField.focused -> {
                            modal.colorField.focused = false
                            modal.maxHpField.focused = true
                        }

                        modal.maxHpField.focused -> {
                            modal.maxHpField.focused = false
                            modal.nameField.focused = true
                        }
                    }
                }

                GLFW.GLFW_KEY_ESCAPE -> modal.close()
            }

            return true
        }

        return super.onScramKeyPress(keyCode, scanCode, modifiers)
    }

    override fun onScramCharType(char: Char): Boolean {
        if (modal.open) {
            when {
                modal.nameField.focused -> modal.nameField.handleChar(char)
                modal.colorField.focused && (char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F') -> modal.colorField.handleChar(char)
                modal.maxHpField.focused && (char.isDigit() || char == '-') -> modal.maxHpField.handleChar(char)
            }

            return true
        }

        return super.onScramCharType(char)
    }

    override fun onScramMouseScroll(mouseX: Int, mouseY: Int, horizontal: Double, vertical: Double): Boolean {
        if (modal.open) {
            if (modal.dropdown) modal.scroll((vertical * 10).toInt())
            return true
        }
        if (entries.isEmpty()) return false
        listRenderer.scroll((vertical * 10).toInt())
        return true
    }

    private fun save() {
        val name = modal.nameField.value.trim()
        if (name.isEmpty()) return

        val color = modal.colorField.value.removePrefix("#").toIntOrNull(16) ?: return
        val max = modal.maxHpField.value.toIntOrNull() ?: -1

        if (modal.typed) {
            if (modal.entry == null) {
                val type = EntityType.byString(name).orElse(null) ?: return
                MobHighlight.e1.update { add(MobHighlight.EntityTyped(type, color, max)) }
            } else {
                val type = EntityType.byString(name).orElse(null) ?: return
                MobHighlight.e1.update { set(modal.entry!!.index, MobHighlight.EntityTyped(type, color, max)) }
            }
        } else {
            if (modal.entry == null) MobHighlight.e0.update { add(MobHighlight.EntityNamed(name, color, max)) }
            else MobHighlight.e0.update { set(modal.entry!!.index, MobHighlight.EntityNamed(name, color, max)) }
        }

        recreate()
        modal.close()
    }

    private fun recreate() {
        entries.clear()

        for ((i, e) in MobHighlight.e0.value.withIndex()) {
            entries.add(HighlightEntry(i, e.name, e.color, e.max, false))
        }

        for ((i, e) in MobHighlight.e1.value.withIndex()) {
            entries.add(HighlightEntry(i, BuiltInRegistries.ENTITY_TYPE.getKey(e.type).toString(), e.color, e.max, true))
        }
    }
}