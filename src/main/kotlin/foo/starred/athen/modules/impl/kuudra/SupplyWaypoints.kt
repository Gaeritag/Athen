@file:Suppress("Unused")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractBeam
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractFilledBox
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractFrameBox
import foo.starred.athen.config.Category
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.athen.utils.markerAABB
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.toDurationFromMillis
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import java.awt.Color

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object SupplyWaypoints : Module(
    "Supply waypoints",
    "Waypoints for supplies, pickup and drop-off spots.",
    Category.KUUDRA
) {
    private val dropOff by config.switch("Drop off", true)
    private val dropOffColor by config.colorPicker("Drop off color", Color(Catppuccin.Mocha.Green.argb, true)).dependsOn { dropOff }

    private val pickup by config.switch("Pick up", true)
    private val pickupColor by config.colorPicker("Pick up color", Color(Catppuccin.Mocha.Teal.argb, true)).dependsOn { pickup }

    private val fuel by config.switch("Fuel", true)
    private val fuelColor by config.colorPicker("Fuel color", Color(Catppuccin.Mocha.Blue.argb, true)).dependsOn { fuel }

    private val changeColor by config.switch("Detect player proximity", true)
    private val playerColor by config.colorPicker("Nearby color", Color(Catppuccin.Mocha.Peach.argb, true)).dependsOn { changeColor }

    private val customMessages = config.switch("Custom supply messages", true).custom("customMessages")
    private val textStyle by config.textInput("Supply text style", "<gray>➤ <red>#user <r>recovered a supply in <red>#time <gray>(#cur/#max)").dependsOn { customMessages.value }
    private val _unused by config.textParagraph("Variable: <red>#user<r>, <red>#time<r>, <red>#cur<r>, <red>#max").dependsOn { customMessages.value }

    private val supplyRegex = Regex("(?:\\[[^]]*] )?(?<user>\\w+) recovered one of Elle's supplies! \\((?<cur>\\d+)/(?<max>\\d+)\\)")

    init {
        on<MessageEvent.Chat.Intercept> {
            if (KuudraAPI.phase != KuudraPhase.Supply) return@on

            supplyRegex.findOrNull(stripped, "user", "cur", "max") { (user, cur, max) ->
                cancel()

                textStyle
                    .replace("#user", user)
                    .replace("#time", KuudraPhase.Supply.durTime.toDurationFromMillis(secondsDecimals = 1))
                    .replace("#cur", cur)
                    .replace("#max", max)
                    .parse(true)
                    .lie()
            }
        }.runWhen(customMessages.state)

        on<WorldRenderEvent.Extract> {
            if (!KuudraAPI.inRun) return@on
            if (!dropOff && !pickup && !fuel) return@on
            val phase = KuudraAPI.phase ?: return@on
            if (phase != KuudraPhase.Supply && phase != KuudraPhase.Fuel) return@on

            when (phase) {
                KuudraPhase.Supply if (dropOff || pickup) -> {
                    if (dropOff) {
                        for (b in KuudraSupply.every) if (!b.active) extractFilledBox(b.buildAABB, dropOffColor.rgb, false)
                    }

                    if (pickup) {
                        for (s in KuudraAPI.supplies) {
                            val color = if (changeColor && s.nearby) playerColor else pickupColor
                            extractFrameBox(s.blockPos.markerAABB(), color.rgb, depth = false)
                            extractBeam(s.blockPos, color.rgb)
                        }
                    }
                }

                KuudraPhase.Fuel if fuel -> {
                    for (s in KuudraAPI.fuels) {
                        val color = if (changeColor && s.nearby) playerColor else fuelColor
                        extractFrameBox(s.blockPos.markerAABB(), color.rgb, depth = false)
                        extractBeam(s.blockPos, color.rgb)
                    }
                }

                else -> {}
            }
        }
    }
}