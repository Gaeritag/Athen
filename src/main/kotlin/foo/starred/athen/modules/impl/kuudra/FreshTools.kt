@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.config.Category
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.command
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.showTitle
import foo.starred.snowbird.utils.toDurationFromMillis
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object FreshTools : Module(
    "Fresh tools",
    "Fresh notifier and timer for kuudra.",
    Category.KUUDRA
) {
    private val alert by config.switch("Show alert", true)
    private val `alert$message` by config.switch("Alert message", true)
    private val `alert$message$t` by config.input("Message", "<red>Fresh tools!")
    private val `alert$title` by config.switch("Alert title", true)
    private val `alert$title$t` by config.input("Title", "<red>Fresh tools!")

    private val notify0 by config.group("Notifications")
    private val notify by notify0.switch("Notify party", true)
    private val `notify$message` by notify0.input("Notify message", "FRESH [#buildPerc]")
    private val `notify$unused` by notify0.variables("#buildPerc")
    private val `notify$checkParty` by notify0.switch("Check party", true)

    private val ex0 = "Fresh: §c6.7s".fcs
    private val timer = config.hud("Fresh timer") {
        if (it) return@hud sizedText(ex0)
        if (time == -1L) return@hud null

        val r = 10_000 - (System.currentTimeMillis() - time)
        if (r <= 0) return@hud fn()

        sizedText("Fresh: §c${r.toDurationFromMillis(secondsDecimals = 1)}")
    }

    private var time: Long = -1

    init {
        on<LocationEvent.Server.Connect> {
            fn()
        }

        on<MessageEvent.Chat.Receive> {
            if (!timer.enabled && !alert) return@on
            if (KuudraAPI.phase != KuudraPhase.Build) return@on
            if (stripped != "Your Fresh Tools Perk bonus doubles your building speed for the next 10 seconds!") return@on

            time = System.currentTimeMillis()

            if (!alert && !notify) return@on
            if (alert && `alert$message`) `alert$message$t`.mod()
            if (alert && `alert$title`) `alert$title$t`.parse().showTitle()
            if (notify && (!`notify$checkParty` || PartyAPI.inParty)) fn0()
        }
    }

    private fun fn(): Pair<Int, Int>? {
        time = -1
        return null
    }

    private fun fn0() {
        val vector = client.player?.blockPosition() ?: return
        val int = KuudraSupply.every.minByOrNull { it.buildPos.distSqr(vector) }?.progress ?: return
        val a = `notify$message`.replace("#buildPerc", "$int%")
        "pc $a".command(false)
    }
}