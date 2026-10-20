package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.util.FormattedCharSequence

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object KuudraTimers : Module(
    "Kuudra timers",
    "Timers for various miscellaneous things in Kuudra.",
    ConfigCategory.KUUDRA
) {
    //<editor-fold desc="Supply timer">
    private var t0 = 0L

    private val d0 = Ticking(2) {
        if (KuudraAPI.phase != KuudraPhase.Supply) return@Ticking null
        val t = (t0 - System.currentTimeMillis()).takeIf { it > 0 }?.div(1000) ?: return@Ticking fn0()
        s0(t.parseDuration(decimals = 1))
    }

    private val spawn by config.hud("Supply spawn timer") {
        val example = "Supply in: <red>4.5s".parse().visualOrderText

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (t0 == 0L) return@render
            MinecraftFontRenderer.extract(graphics, d0.value ?: return@render, 0, 0)
        }
    }

    private val spawnStyle by config.input("Supply text style", "Supply in: <red>#time")
    //</editor-fold>

    //<editor-fold desc="Build timer">
    private var t1 = 0L

    private val d1 = Ticking(2) {
        if (KuudraAPI.phase != KuudraPhase.Build) return@Ticking null
        val t = (t1 - System.currentTimeMillis()).takeIf { it > 0 }?.div(1000) ?: return@Ticking fn1()
        s1(t.parseDuration(decimals = 1))
    }

    private val build by config.hud("Build start timer") {
        val example = "Build in: <red>4.5s".parse().visualOrderText

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (t1 == 0L) return@render
            MinecraftFontRenderer.extract(graphics, d1.value ?: return@render, 0, 0)
        }
    }

    private val buildStyle by config.input("Build text style", "Build in: <red>#time")
    //</editor-fold>

    init {
        on<KuudraEvent.Phase.Supply> {
            if (spawn.state.value) t0 = System.currentTimeMillis() + 8900
        }

        on<KuudraEvent.Phase.Build> {
            if (build.state.value) t1 = System.currentTimeMillis() + 5100
        }

        on<LocationEvent.Server.Connect> {
            fn()
        }
    }

    //<editor-fold desc = "Reset">
    private fun fn() {
        fn0()
        fn1()
    }

    private fun fn0(): FormattedCharSequence? {
        t0 = 0L
        return null
    }

    private fun fn1(): FormattedCharSequence? {
        t1 = 0L
        return null
    }
    //</editor-fold>

    //<editor-fold desc = "Format">
    private fun s0(t: String): FormattedCharSequence = spawnStyle
        .replace("#time", t)
        .parse(true)
        .visualOrderText

    private fun s1(t: String): FormattedCharSequence = buildStyle
        .replace("#time", t)
        .parse(true)
        .visualOrderText
    //</editor-fold>
}
