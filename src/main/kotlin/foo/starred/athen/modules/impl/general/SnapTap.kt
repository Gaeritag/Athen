package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.events.InputEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.override
import foo.starred.athen.mixin.accessors.KeyMappingAccessor
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.client
import net.minecraft.client.KeyMapping

@Load
object SnapTap : Module(
    "Snap-Tap",
    "Snap Tap allows you to strafe while continuing to hold the initial key and quickly tapping the opposing key.",
    Category.GENERAL
) {
    private val active = HashSet<Int>(4)
    private val pairs = ArrayList<Pair>(4)

    private data class Pair(
        val curr: KeyMapping,
        val oppo: KeyMapping
    ) {
        val key: Int
            get() = (curr as KeyMappingAccessor).boundKey.value

        val opp: Int
            get() = (oppo as KeyMappingAccessor).boundKey.value
    }

    init {
        on<InputEvent.Keyboard.Press> {
            //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
            if (client.screen != null) return@on

            val key = keyEvent.key()
            if (active.add(key)) key.pair(false)
        }

        on<InputEvent.Keyboard.Release> {
            //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
            if (client.screen != null) return@on active.clear()

            val key = keyEvent.key()
            if (active.remove(key)) key.pair(true)
        }

        on<TickEvent.Client.End> {
            val options = client.options ?: return@on
            with (pairs) {
                add(Pair(options.keyLeft, options.keyRight))
                add(Pair(options.keyRight, options.keyLeft))
                add(Pair(options.keyUp, options.keyDown))
                add(Pair(options.keyDown, options.keyUp))
            }
        }.override().once()
    }

    private fun Int.pair(bool: Boolean) {
        for (pair in pairs) {
            if (pair.key != this) continue
            if (pair.opp !in active) return

            pair.oppo.isDown = bool
            return
        }
    }
}