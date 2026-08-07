package foo.starred.athen.modules.impl.general.keybinds

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.events.InputEvent
import foo.starred.athen.handlers.Scribble
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.general.keybinds.data.CategoryEntry
import foo.starred.athen.modules.impl.general.keybinds.data.KeybindCondition
import foo.starred.athen.modules.impl.general.keybinds.data.KeybindEntry
import foo.starred.athen.modules.impl.general.keybinds.ui.KeybindsGUI
import foo.starred.athen.utils.command
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.command
import foo.starred.snowbird.api.message

@Load
object Keybinds : Module(
    "Keybinds",
    "Keybinds and shortcuts for various actions.",
    Category.GENERAL
) {
    private val keys = mutableSetOf<Int>()
    private val buttons = mutableSetOf<Int>()
    private val triggered = mutableSetOf<KeybindEntry>()

    @Suppress("unused")
    private val _unused by config.button("Open manager") { client.setScreen(KeybindsGUI) }

    val storage = Scribble("features/Keybinds")
    var bindings = storage.mutableList("bindings", KeybindEntry.CODEC)
    var categories = storage.mutableList("categories", CategoryEntry.CODEC)

    init {
        command {
            "keybinds" {
                KeybindsGUI.open()
            }
        }

        on<InputEvent.Keyboard.Press> {
            keys.add(keyEvent.key)
            check()
        }

        on<InputEvent.Keyboard.Release> {
            keys.remove(keyEvent.key)
            reset()
        }

        on<InputEvent.Mouse.Press> {
            buttons.add(buttonInfo.button)
            check()
        }

        on<InputEvent.Mouse.Release> {
            buttons.remove(buttonInfo.button)
            reset()
        }
    }

    private fun check() {
        val all = (keys + buttons).toHashSet()
        val disabled = categories.value.filter { !it.enabled }.map { it.name }

        for (binding in bindings.value) {
            if (!binding.condition.eval()) continue

            val ks = binding.keys
            if (ks.isEmpty()) continue
            if (binding in triggered) continue
            if (!binding.enabled) continue
            if (binding.category.isNotEmpty() && binding.category in disabled) continue
            if (!ks.all(all::contains)) continue

            triggered.add(binding)

            val command = binding.command
            if (command.isEmpty()) continue
            if (command[0] == '/') command.command() else command.message()
        }
    }

    private fun reset() {
        val pressed = keys + buttons
        triggered.removeIf { b -> b.keys.any { it !in pressed } }
    }

    fun List<Int>.add(command: String, category: String = "", condition: KeybindCondition = KeybindCondition()): Boolean {
        if (command.isBlank() || isEmpty()) return false
        bindings.update { add(KeybindEntry(this@add, command, true, category, condition)) }
        return true
    }

    fun Int.remove(): Boolean {
        if (this !in bindings.value.indices) return false
        bindings.update { removeAt(this@remove) }
        return true
    }

    fun Int.update(keys: List<Int>, command: String, enabled: Boolean, category: String = "", condition: KeybindCondition): Boolean {
        if (this !in bindings.value.indices || command.isBlank() || keys.isEmpty()) return false
        val int = this
        bindings.update { set(int, KeybindEntry(keys, command, enabled, category, condition)) }
        return true
    }

    fun addCategory(name: String): Boolean {
        if (name.isBlank() || categories.value.any { it.name == name }) return false
        categories.update { add(CategoryEntry(name)) }
        return true
    }

    fun removeCategory(name: String) {
        categories.update { removeIf { it.name == name } }
        bindings.update {
            val updated = map { if (it.category == name) it.copy(category = "") else it }
            clear()
            addAll(updated)
        }
    }

    fun toggleCategory(name: String) {
        categories.update {
            val idx = indexOfFirst { it.name == name }
            if (idx >= 0) set(idx, get(idx).copy(enabled = !get(idx).enabled))
        }
    }
}