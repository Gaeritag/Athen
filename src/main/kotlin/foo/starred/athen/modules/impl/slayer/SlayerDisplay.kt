package foo.starred.athen.modules.impl.slayer

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.config.Category
import foo.starred.athen.ducks.entity.EntityDuck.Companion.attachedNames
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.handlers.Ticking
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.utils.stripped

@Load
@OnlyIn(skyblock = true)
object SlayerDisplay : Module(
    "Slayer display",
    "Displays the slayer boss's nametags on your screen.",
    Category.SLAYER
) {
    private val ex0 = listOf("§c02:46", "§c☠ §bRevenant Horror I §a500§c❤").fcs

    private var displayComponents: List<Component>? = null
    private var slayerEntity: Entity? = null

    private val display = Ticking(2) {
        val entity = slayerEntity ?: return@Ticking null

        val lines = entity.attachedNames
        var colon: Component? = null
        var name: Component? = null

        for (l in lines) {
            val s = l.stripped()
            if ("Spawned by:" in s) continue

            colon = colon ?: l.takeIf { ":" in s }
            name = name ?: l.takeIf { SlayerBoss.NAMES.any { it in s } }

            if (colon != null && name != null) break
        }

        listOfNotNull(colon, name)
    }

    init {
        config.hud("Display HUD") {
            if (it) return@hud sizedText(ex0, center = listOf(0))
            sizedText(display.value ?: return@hud null, center = listOf(0))
        }

        on<SlayerEvent.Boss.Spawn> {
            if (slayerInfo.owned) slayerEntity = entity
        }

        on<SlayerEvent.Boss.Death> {
            if (slayerInfo.owned) reset()
        }

        on<SlayerEvent.Reset.Any> {
            reset()
        }
    }

    private fun reset() {
        slayerEntity = null
        displayComponents = null
    }
}