@file:Suppress("Unused")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.aabb
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.text.parser.impl.parse
import foo.starred.parallax.api.primitives.ParallaxBeam
import foo.starred.parallax.api.primitives.ParallaxBox

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object SupplyWaypoints : Module(
    "Supply waypoints",
    "Waypoints for supplies, pickup and drop-off spots.",
    ConfigCategory.KUUDRA
) {
    private val dropOff by config.switch("Drop off", true)
    private val dropOffColor by config.colorPicker("Drop off color", MochaColorScheme.Green.argb)

    private val pickup by config.switch("Pick up", true)
    private val pickupColor by config.colorPicker("Pick up color", MochaColorScheme.Teal.argb)

    private val fuel by config.switch("Fuel", true)
    private val fuelColor by config.colorPicker("Fuel color", MochaColorScheme.Blue.argb)

    private val changeColor by config.switch("Detect player proximity", true)
    private val playerColor by config.colorPicker("Nearby color", MochaColorScheme.Peach.argb)

    private val customMessages = config.switch("Custom supply messages", true)
    private val textStyle by config.input("Supply text style", "<gray>➤ <red>#user <r>recovered a supply in <red>#time <gray>(#cur/#max)")
    private val _unused by config.variables("#user", "#time", "#cur", "#max")

    private val supplyRegex = Regex("(?:\\[[^]]*] )?(?<user>\\w+) recovered one of Elle's supplies! \\((?<cur>\\d+)/(?<max>\\d+)\\)")

    init {
        on<MessageEvent.Chat.Intercept> {
            if (KuudraAPI.phase != KuudraPhase.Supply) return@on

            val (user, cur, max) = supplyRegex.find(stripped)?.destructured ?: return@on
            cancel()

            MinecraftMessages.display(
                textStyle
                    .replace("#user", user)
                    .replace("#time", KuudraPhase.Supply.durTime.div(1000).parseDuration(decimals = 1))
                    .replace("#cur", cur)
                    .replace("#max", max)
                    .parse(true)
            )
        }.runWhen(customMessages.state)

        on<WorldRenderEvent.Extract> {
            if (!KuudraAPI.inRun) return@on
            if (!dropOff && !pickup && !fuel) return@on
            val phase = KuudraAPI.phase ?: return@on
            if (phase != KuudraPhase.Supply && phase != KuudraPhase.Fuel) return@on

            when (phase) {
                KuudraPhase.Supply if (dropOff || pickup) -> {
                    if (dropOff) {
                        for (b in KuudraSupply.every) {
                            if (b.active) continue
                            ParallaxBox.fill(b.buildAABB, dropOffColor, false)
                        }
                    }

                    if (pickup) {
                        for (s in KuudraAPI.supplies) {
                            val color = if (changeColor && s.nearby) playerColor else pickupColor
                            ParallaxBox.frame(s.blockPos.aabb(), color, depth = false)
                            ParallaxBeam.extract(s.blockPos, color)
                        }
                    }
                }

                KuudraPhase.Fuel if fuel -> {
                    for (s in KuudraAPI.fuels) {
                        val color = if (changeColor && s.nearby) playerColor else fuelColor
                        ParallaxBox.frame(s.blockPos.aabb(), color, depth = false)
                        ParallaxBeam.extract(s.blockPos, color)
                    }
                }

                else -> {}
            }
        }
    }
}
