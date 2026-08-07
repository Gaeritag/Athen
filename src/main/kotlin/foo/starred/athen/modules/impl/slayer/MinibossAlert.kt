@file:Suppress("Unused")

package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.slayers.enums.type.impl.SlayerMini
import foo.starred.athen.config.Category
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.handlers.Notifier.notify
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.alert
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup

@Load
@OnlyIn(skyblock = true)
object MinibossAlert : Module(
    "Miniboss alert",
    "Shows an alert for you when a miniboss spawns nearby.",
    Category.SLAYER
) {
    private val detection = config.dropdown("Detection type", listOf("Chat based", "Event based"), 1).custom("detection")
    private val _unused by config.textParagraph("Chat based detection type only works for your minibosses. Event based detection type works for all minibosses near you.")
    private val sendMessage by config.switch("Send message", true)
    private val vanillaMessage by config.switch("Use mc message").dependsOn { sendMessage }
    private val showTitle by config.switch("Show title", true)
    private val maxDistance by config.slider("Maximum distance", 10, 1, 15, "blocks")
    private val alertText by config.textInput("Alert text", "<aqua>Miniboss spawned!")
    private val bigBoiText by config.textInput("Big boi text", "<red>Big boi spawned!")
    private val _unused0 by config.textParagraph("The same text will be used for both title and message.\n<gray>Big boi = Big miniboss")

    private val bigBoys = SlayerMini.entries.filter { it.special }.map { it.name }
    private val regex = Regex("^SLAYER MINI-BOSS (?<name>.+?) has spawned!$")

    init {
        on<MessageEvent.Chat.Receive> {
            val name = regex.findGroup(stripped, "name") ?: return@on
            val text = if (name in bigBoys) bigBoiText else alertText

            if (showTitle) text.parse().alert()
            if (sendMessage) if (vanillaMessage) text.parse().modMessage() else text.notify()
        }.runWhen(detection.state.map { it == 0 })

        on<SlayerEvent.Miniboss.Spawn> {
            if (entity.tickCount >= 20) return@on
            val player = client.player ?: return@on
            val slayerMiniBoss = (slayerInfo.type as? SlayerMini).takeIf { entity.distanceTo(player) < maxDistance } ?: return@on
            val text = (if (slayerMiniBoss.special) bigBoiText else alertText)

            if (showTitle) text.parse().alert()
            if (sendMessage) if (vanillaMessage) text.parse().modMessage() else text.notify()
        }.runWhen(detection.state.map { it == 1 })
    }
}