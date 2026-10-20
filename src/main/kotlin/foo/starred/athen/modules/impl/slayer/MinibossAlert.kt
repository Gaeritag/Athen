@file:Suppress("Unused")

package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.slayers.enums.type.impl.SlayerMini
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.title.MinecraftTitle
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object MinibossAlert : Module(
    "Miniboss alert",
    "Shows an alert for you when a miniboss spawns nearby.",
    ConfigCategory.SLAYER
) {
    private val detection = config.selector("Detection type", listOf("Chat based", "Event based"), 1).unique("detection").description("Chat based for your minibosses. Event based for all minibosses near you.")
    private val sendMessage by config.switch("Send message", true)
    private val vanillaMessage by config.switch("Use mc message")
    private val showTitle by config.switch("Show title", true)
    private val maxDistance by config.slider("Maximum distance", 10, 1, 15, "blocks")
    private val alertText by config.input("Alert text", "<aqua>Miniboss spawned!")
    private val bigBoiText by config.input("Special text", "<red>Big boi spawned!")

    private val special = SlayerMini.entries.filter { it.special }.map { it.name }
    private val regex = Regex("^SLAYER MINIBOSS! The (?<name>.+?) spawned!$")

    init {
        on<MessageEvent.Chat.Receive> {
            val (name) = regex.find(stripped)?.destructured ?: return@on
            val text = if (name in special) bigBoiText else alertText

            fn(text)
        }.runWhen(detection.state.map { it == 0 })

        on<SlayerEvent.Miniboss.Spawn> {
            if (entity.tickCount >= 20) return@on
            val player = client.player ?: return@on
            val info = (slayerInfo.type as? SlayerMini).takeIf { entity.distanceTo(player) < maxDistance } ?: return@on
            val text = (if (info.special) bigBoiText else alertText)

           fn(text)
        }.runWhen(detection.state.map { it == 1 })
    }

    private fun fn(text: String) {
        if (showTitle) {
            MinecraftTitle.display(text.parse())
        }

        if (sendMessage) {
            text.mod()
        }
    }
}
