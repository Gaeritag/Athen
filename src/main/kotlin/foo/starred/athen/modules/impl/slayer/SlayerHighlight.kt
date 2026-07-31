@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.slayer

import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractFrameBox
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.Category
import foo.starred.athen.ducks.entity.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.renderBoundingBox
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@Load
@OnlyIn(skyblock = true)
object SlayerHighlight : Module(
    "Slayer highlights",
    "Highlights the slayer bosses.",
    Category.SLAYER
) {
    private val regex = Regex("^(?<attunement>[A-Z]+) ♨(\\d+) \\d\\d:\\d\\d$")

    private val boss by config.switch("Highlight boss")
    private val `boss$mine` by config.switch("Only for mine", true).dependsOn { boss }
    private val `boss$color` by config.colorPicker("Color", Color(255, 0, 0, 255)).dependsOn { boss }
    private val `boss$width` by config.slider("Line width", 2f, 0f, 10f).dependsOn { boss }

    private val mini by config.switch("Highlight miniboss", false)
    private val `mini$color` by config.colorPicker("Miniboss color", Color(255, 127, 127, 255)).dependsOn { mini }
    private val `mini$width` by config.slider("Miniboss line width", 2f, 0f, 10f).dependsOn { mini }

    private val demon by config.switch("Highlight demon", false)
    private val `demon$color` by config.colorPicker("Demon color", Color(255, 165, 0, 255)).dependsOn { demon }
    private val `demon$width` by config.slider("Demon line width", 2f, 0f, 10f).dependsOn { demon }

    private val blaze by config.switch("Blaze state colors", true)
    private val `blaze$ashen` by config.colorPicker("Ashen", Color(TextColor.DARK_GRAY)).dependsOn { blaze }
    private val `blaze$auric` by config.colorPicker("Auric", Color(TextColor.GOLD)).dependsOn { blaze }
    private val `blaze$crystal` by config.colorPicker("Crystal", Color(TextColor.AQUA)).dependsOn { blaze }
    private val `blaze$spirit` by config.colorPicker("Spirit", Color(TextColor.WHITE)).dependsOn { blaze }

    private val slayers = ConcurrentHashMap<Entity, Int>()
    private val demons = ConcurrentHashMap<Entity, Int>()
    private val minibosses = mutableMapOf<Entity, Int>()

    init {
        on<TickEvent.Client.End> {
            if (ticks % 5 != 0) return@on

            slayers.keys.removeIf { !it.isAlive }
            demons.keys.removeIf { !it.isAlive }
            minibosses.keys.removeIf { !it.isAlive }
        }

        on<EntityEvent.Update.Named> {
            val a = entity in slayers.keys
            val b = entity in demons.keys
            if (!a && !b) return@on

            val e = entity.parent ?: return@on
            val s = SlayerAPI.bosses[e] ?: return@on
            if (!b && s.owner == null) return@on

            val f = regex.findGroup(stripped, "attunement") ?: return@on
            val c = (if (f == "ASHEN") `blaze$ashen` else if (f == "AURIC") `blaze$auric` else if (f == "CRYSTAL") `blaze$crystal` else if (f == "SPIRIT") `blaze$spirit` else `boss$color`).rgb

            if (a) slayers[e] = c
            if (b) demons[e] = c
        }

        on<SlayerEvent.Boss.Spawn> {
            if (!boss) return@on
            if (`boss$mine` && !slayerInfo.owned) return@on

            slayers[entity] = `boss$color`.rgb
        }

        on<SlayerEvent.Miniboss.Spawn> {
            if (!mini) return@on

            minibosses[entity] = `mini$color`.rgb
        }

        on<SlayerEvent.Demon.Spawn> {
            if (!demon) return@on

            demons[entity] = `demon$color`.rgb
        }

        on<SlayerEvent.Boss.Death> {
            slayers -= entity
        }

        on<SlayerEvent.Miniboss.Death> {
            minibosses -= entity
        }

        on<SlayerEvent.Demon.Death> {
            demons -= entity
        }

        on<LocationEvent.Server.Connect> {
            slayers.clear()
            minibosses.clear()
            demons.clear()
        }

        on<WorldRenderEvent.Extract> {
            slayers.fn(`boss$width`)
            minibosses.fn(`mini$width`)
            demons.fn(`demon$width`)
        }
    }

    private fun Map<Entity, Int>.fn(width: Float) {
        val map = this
        for ((k, v) in map) {
            if (!k.isAlive) continue
            extractFrameBox(k.renderBoundingBox, v, width)
        }
    }
}