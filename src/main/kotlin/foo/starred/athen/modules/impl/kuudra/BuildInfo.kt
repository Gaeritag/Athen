@file:Suppress("Unused", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.abstraction.title.MinecraftTitle
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.text.parser.impl.parse
import foo.starred.parallax.api.primitives.ParallaxBox

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object BuildInfo : Module(
    "Build info",
    "Shows information about the ballista build process in phase 2.",
    ConfigCategory.KUUDRA
) {
    private val waypoints = config.switch("Unfinished build waypoint", true).unique("waypoints")
    private val color by config.colorPicker("Color", MochaColorScheme.Red.argb)
    private val stun by config.switch("Stun notification", true)
    private val `stun$percent` by config.slider("Notify at", 90, 1, 100, "%")
    private val `stun$message` by config.input("Notification message", "<red>Stun!")

    private val display = Ticking {
        listOf("§7Builders: §c${KuudraAPI.buildPlayers}", "§7Progress: §c${KuudraAPI.buildProgress.value}%").formattedText()
    }

    private val hud by config.hud("Build info") {
        val example = listOf("§7Builders: §c3", "§7Progress: §c47%").formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            if (!render) return@render

            MinecraftFontRenderer.extract(graphics, display.value ?: return@render, 0, 0)
        }
    }


    private val render: Boolean
        get() = KuudraAPI.inRun && KuudraAPI.phase == KuudraPhase.Build

    private var sent: Boolean = false

    init {
        KuudraAPI.buildProgress.observe {
            if (!stun) return@observe
            if (sent) return@observe
            if (it <= `stun$percent`) return@observe

            val prs = `stun$message`.parse(true)
            MinecraftTitle.display(prs)
            prs.mod()
            sent = true
        }

        on<KuudraEvent.Start> {
            sent = false
        }

        on<WorldRenderEvent.Extract> {
            if (!render) return@on

            for (e in KuudraSupply.every) {
                if (e.built) continue
                ParallaxBox.frame(e.buildAABB, color, depth = false)
            }
        }.runWhen(waypoints.state)
    }
}
