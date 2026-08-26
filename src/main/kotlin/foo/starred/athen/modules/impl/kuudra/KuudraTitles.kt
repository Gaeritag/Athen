@file:Suppress("Unused", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.config.Category
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.alert
import foo.starred.snowbird.utils.literal
import net.minecraft.network.chat.Component

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object KuudraTitles : Module(
    "Kuudra titles",
    "Custom alerts and titles for kuudra!",
    Category.KUUDRA
) {
    private val supply0 by config.group("Supply titles")
    private val supply = supply0.hud("Supply titles") {
        if (it) return@hud sizedText(dis0 ?: _dis)
        if (KuudraAPI.phase !in KuudraAPI.set) return@hud null

        val display = display ?: return@hud null
        sizedText(display)
    }

    private val supplyStyle = supply0.input("Supply text style", "<dark_gray>[<green>#bars<gray>#total <r>- <aqua>#perc%<dark_gray>]").unique("supplyStyle")
    private val `barCharacter$filled` by supply0.input("Filled bar character", "|")
    private val `barCharacter$left` by supply0.input("Left bar character", "|")
    private val `barCharacter$total` by supply0.slider("Number", 20, 5, 30, "bars")
    private val _unused by supply0.variables("#bars", "#total", "#perc")

    private val alerts by config.group("Alerts")
    private val dropAlert by alerts.switch("Drop alert", true)
    private val dropMessage by alerts.input("Drop alert message", "<red>Dropped supply!")
    private val pickupAlert by alerts.switch("Pick up alert")
    private val pickMessage by alerts.input("Pick up alert message", "<green>Picked up supply!")

    private val _dis: Component = "§8[§a|||||||||§f|||||||||§8] §b67%".literal()
    private var dis0: Component? = null
    private var display: Component? = null

    init {
        supplyStyle.state.onChange { dis0 = 20.str() }.also { dis0 = 20.str() }

        on<KuudraEvent.Supply.Progress> {
            if (!supply.enabled) return@on

            display = progress.str()
            cancel()
        }

        on<KuudraEvent.Supply.Pickup> {
            display = null
            if (pickupAlert) pickMessage.parse().alert()
        }

        on<KuudraEvent.Supply.Drop> {
            display = null
            if (dropAlert) dropMessage.parse().alert()
        }
    }

    private fun Int.str(): Component {
        val f = (coerceIn(0, 100) * `barCharacter$total`) / 100

        return supplyStyle.value
            .replace("#perc", toString())
            .replace("#bars", `barCharacter$filled`.repeat(f))
            .replace("#total", `barCharacter$left`.repeat(`barCharacter$total` - f))
            .parse()
    }
}
