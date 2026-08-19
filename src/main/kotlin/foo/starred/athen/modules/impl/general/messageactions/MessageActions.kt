@file:Suppress("Unused")

package foo.starred.athen.modules.impl.general.messageactions

import com.google.gson.reflect.TypeToken
import foo.starred.athen.Athen
import foo.starred.athen.Athen.GSON
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.api.storage.JsonStore
import foo.starred.athen.config.Category
import foo.starred.athen.events.GameEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.general.messageactions.actions.impl.NoAction
import foo.starred.athen.modules.impl.general.messageactions.data.ActionEntry
import foo.starred.athen.modules.impl.general.messageactions.data.CategoryEntry
import foo.starred.athen.modules.impl.general.messageactions.data.MatchType
import foo.starred.athen.modules.impl.general.messageactions.data.ResolvedEntry
import foo.starred.athen.modules.impl.general.messageactions.ui.MessageActionsGUI
import foo.starred.athen.utils.command
import foo.starred.snowbird.utils.compress
import foo.starred.snowbird.utils.decompress
import foo.starred.snowbird.utils.safely
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Load
object MessageActions : Module(
    "Message actions",
    "Allows you to run actions when you receive a message.",
    Category.GENERAL
) {
    private val _unused by config.button("Open manager") { MessageActionsGUI.open() }
    private val _unused0 by config.information("You can use the commands <red>\"/${Athen.modId} [import|export] messageactions\"<r> to share configs!")

    private val json = JsonStore("features/MessageActions")
    private var _actions: String by json.string("actions")
    private var _categories: String by json.string("categories")

    private var set = emptySet<String>()
    private var a0 = emptyArray<ResolvedEntry>()
    private var a1 = emptyArray<ResolvedEntry>()

    val actions = ObjectArrayList<ActionEntry>()
    val categories = ObjectArrayList<CategoryEntry>()

    init {
        on<GameEvent.Start> {
            safely {
                val ra = _actions.takeIf { it.isNotBlank() }
                if (ra != null) {
                    actions.clear()
                    actions.addAll(GSON.fromJson<List<ActionEntry>>(ra, object : TypeToken<List<ActionEntry>>() {}.type))
                }

                val rc = _categories.takeIf { it.isNotBlank() }
                if (rc != null) {
                    categories.clear()
                    categories.addAll(GSON.fromJson<List<CategoryEntry>>(rc, object : TypeToken<List<CategoryEntry>>() {}.type))
                }

                fn()
            }
        }

        on<GameEvent.Stop> {
            disk()
        }

        on<MessageEvent.Chat.Intercept> {
            for (entry in a0) {
                if (!entry.matches(stripped, set)) continue
                cancel()

                if (entry.action == NoAction) return@on
                val action = entry.action() ?: continue
                if (entry.source.delay > 0.0) Scheduler.schedule(entry.delay) { action.run() }
                else action.run()
            }
        }

        on<MessageEvent.Chat.Receive> {
            for (entry in a1) {
                if (!entry.matches(stripped, set)) continue

                val action = entry.action() ?: continue
                if (entry.source.delay > 0.0) Scheduler.schedule(entry.delay) { action.run() }
                else action.run()
            }
        }

        command {
            "messageactions" {
                MessageActionsGUI.open()
            }

            "messageactions" / "gui" {
                MessageActionsGUI.open()
            }

            "export" / "messageactions" {
                disk()
                McClient.clipboard = GSON.toJson(mapOf("actions" to actions, "categories" to categories)).compress()
                "Exported ${actions.size} actions to clipboard!".mod()
            }

            "import" / "messageactions" {
                val a = McClient.clipboard
                if (a.isEmpty()) return@invoke "No data found in clipboard!".mod()

                safely {
                    val map = GSON.fromJson(a.decompress(), object : TypeToken<Map<String, Any>>() {}.type) as Map<String, Any>
                    val b = GSON.fromJson<List<ActionEntry>>(GSON.toJson(map["actions"]), object : TypeToken<List<ActionEntry>>() {}.type)
                    val c = GSON.fromJson<List<CategoryEntry>>(GSON.toJson(map["categories"]), object : TypeToken<List<CategoryEntry>>() {}.type)

                    actions.clear()
                    actions.addAll(b)
                    categories.clear()
                    categories.addAll(c)

                    fn()
                    disk()
                    "Imported ${b.size} actions and ${c.size} categories!".mod()
                }
            }
        }
    }

    fun fn() {
        set = HashSet<String>(categories.size).also { set ->
            for (c in categories) if (!c.enabled) set.add(c.name)
        }

        val a = ObjectArrayList<ResolvedEntry>()
        val b = ObjectArrayList<ResolvedEntry>()
        for (a0 in actions) {
            val r = ResolvedEntry(a0)
            if (a0.cancel) a.add(r) else if (a0.id != NoAction.id) b.add(r)
        }

        a0 = a.toArray(emptyArray())
        a1 = b.toArray(emptyArray())
    }

    fun disk() {
        _actions = GSON.toJson(actions)
        _categories = GSON.toJson(categories)
    }

    fun add(pattern: String, match: MatchType, id: Int, value: String, category: String, cancel: Boolean, delay: Double): Boolean {
        if (pattern.isBlank()) return false
        actions.add(ActionEntry(pattern, match, id, value, true, category, cancel, delay))
        fn()
        return true
    }

    fun remove(index: Int): Boolean {
        if (index !in actions.indices) return false
        actions.removeAt(index)
        fn()
        return true
    }

    fun update(index: Int, entry: ActionEntry): Boolean {
        if (index !in actions.indices || entry.pattern.isBlank()) return false
        actions[index] = entry
        fn()
        return true
    }

    fun add(name: String): Boolean {
        if (name.isBlank() || categories.any { it.name == name }) return false
        categories.add(CategoryEntry(name))
        fn()
        return true
    }

    fun remove(name: String) {
        categories.removeIf { it.name == name }

        for (i in actions.indices) {
            val a = actions[i].takeIf { it.category == name } ?: continue
            actions[i] = a.copy(category = "")
        }

        fn()
    }

    fun toggle(name: String) {
        for (i in categories.indices) {
            val c = categories[i].takeIf { it.name == name } ?: continue
            categories[i] = c.copy(enabled = !c.enabled)
            break
        }

        fn()
    }
}