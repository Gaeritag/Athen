package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraTier
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.kontext.api.entity.extension.renderVec3
import foo.starred.kontext.api.format.extension.number.abbreviate
import foo.starred.kontext.api.text.parser.impl.parse
import foo.starred.parallax.api.primitives.ParallaxBox
import foo.starred.parallax.api.primitives.ParallaxText
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.LivingEntity
import kotlin.math.abs

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object KuudraInfo : Module(
    "Kuudra info",
    "Displays information about kuudra and highlights him, nicely :3",
    ConfigCategory.KUUDRA
) {
    private val highlight by config.switch("Highlight", true)
    private val lineWidth by config.slider("Line width", 2f, 1f, 10f)
    private val color by config.colorPicker("Color", MochaColorScheme.Peach.argb)
    private val hpOnKuudra by config.switch("Draw hp on boss", true)

    private var display: FormattedCharSequence? = null
    private var health: Float = 0f

    private val hud by config.hud("Kuudra HP") {
        val example = "<green>46.5m<gray>/<dark_red>240m <red>❤".parse().visualOrderText

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (!KuudraAPI.inRun) return@render
            MinecraftFontRenderer.extract(graphics, display ?: return@render, 0, 0)
        }
    }

    init {
        on<LocationEvent.Server.Connect> {
            display = null
            health = 0f
        }

        on<EntityEvent.Update.Health> {
            if (!hud.state.value && !hpOnKuudra) return@on
            if (!KuudraAPI.inRun) return@on
            if (health == new) return@on
            val e = KuudraAPI.kuudra?.takeIf { it == entity } ?: return@on

            display = e.str()
            health = new
        }

        on<TickEvent.Client.End> {
            if (ticks % 10 != 0) return@on
            if (!hud.state.value && !hpOnKuudra) return@on
            if (!KuudraAPI.inRun) return@on
            val kuudra = KuudraAPI.kuudra?.takeIf { it.health != health } ?: return@on

            display = kuudra.str()
            health = kuudra.health
        }

        on<WorldRenderEvent.Extract> {
            render()
        }
    }

    private fun LivingEntity.str(): FormattedCharSequence {
        val s = health
        val bool = KuudraAPI.tier == KuudraTier.INFERNAL && s <= 25_000
        val m = if (bool) 240_000_000f else 100_000f
        val h = if (bool) s * 9_600 else s // 240_000_000 / 25_000 = 9_600
        val m0 = if (bool) "240M" else "100K"

        val color = when (h / m) {
            in 0.7..1.0 -> "<dark_red>"
            in 0.5..0.7 -> "<red>"
            in 0.3..0.5 -> "<green>"
            else -> "<dark_green>"
        }

        return "$color${h.abbreviate()}<gray>/<dark_red>$m0 <red>❤".parse().visualOrderText
    }

    private fun render() {
        if (!KuudraAPI.inRun) return
        val k = KuudraAPI.kuudra ?: return

        if (highlight) {
            ParallaxBox.frame(k.renderAABB(), color, lineWidth)
        }

        if (hpOnKuudra) {
            val text = display ?: return
            val hw = k.bbWidth * 0.5
            val hh = k.bbHeight * 0.5

            val c = k.renderVec3().add(0.0, hh, 0.0)
            //~ if >= 26.2 'mainCamera' -> 'mainCamera()'
            val d = client.gameRenderer.mainCamera.position().subtract(c)

            val t = minOf(fn(d.x, hw), fn(d.y, hh), fn(d.z, hw))

            ParallaxText.formatted(text, c.add(d.scale(t + 0.1)), increase = true)
        }
    }

    private fun fn(d: Double, b: Double): Double {
        return if (d == 0.0) Double.POSITIVE_INFINITY else b / abs(d)
    }
}
