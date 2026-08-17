@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.config.Category
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.alert
import foo.starred.snowbird.utils.toDurationFromMillis

@Load
@OnlyIn(skyblock = true)
object CocoonAlert : Module(
    "Cocoon alert",
    "Alerts you when you cocoon your slayer boss!",
    Category.SLAYER
) {
    private val alert by config.switch("Show alert", true)
    private val `alert$message` by config.input("Alert message", "<red>Boss cocooned!")
    private val `alert$sound` by config.sound("Alert sound")

    private val ex0 = "Cocoon: §c4.6s".fcs
    private val timer = config.hud("Cocoon timer") {
        if (it) return@hud sizedText(ex0)
        if (time == 0L) return@hud null

        val t = time - System.currentTimeMillis()
        if (t <= 0L) {
            time = 0
            return@hud null
        }

        sizedText("Cocoon: §c${t.toDurationFromMillis(secondsDecimals = 1)}")
    }

    private var time: Long = 0

    init {
        on<MessageEvent.Chat.Receive> {
            if (stripped.trim() != "YOU COCOONED YOUR SLAYER BOSS") return@on

            if (alert) `alert$message`.parse().alert(soundType = `alert$sound`.sound)
            if (timer.enabled) time = System.currentTimeMillis() + 6000
        }
    }
}