package foo.starred.athen.modules.impl

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.Athen
import foo.starred.athen.accessors.hovered
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.utils.command
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.generic.MinecraftChat
import foo.starred.kontext.abstraction.client.impl.MinecraftClient
import foo.starred.kontext.abstraction.gui.MinecraftGui
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
object DeveloperModule {
    private val keyRegex = Regex("(?<!^)([A-Z])")
    private val texture = Observable(false)

    init {
        on<GuiEvent.Input.Key.Release> {
            if (keyEvent.key() != InputConstants.KEY_S) return@on

            val container = MinecraftGui.container ?: return@on
            val hovered = container.hovered ?: return@on
            val string = hovered.item.get<GenericKontextItemDataResolver.TEXTURE, String>()

            if (string != null) {
                MinecraftClient.clipboard = string
                texture.value = false
                "Successfully copied texture for hovered item! Disabling...".mod()

                return@on
            }

            texture.value = false
            "Could not find texture for hovered item! Disabling...".mod()
        }.runWhen(texture)
    }

    init {
        command {
            "toggle" / "dev" {
                Athen.Data.debug = !Athen.Data.debug
                val a = if (Athen.Data.debug) "<green>Enabled" else "<red>Disabled"

                "Debug mode is now: $a<r>.".mod()
            }

            "toggle" / "feature" / string("key") {
                val key = string("key")

                val b = ConfigManager.get(key) as? Boolean ?: return@string "Not a valid feature!".mod()
                ConfigManager.update(key, !b)

                val s = key.replace(keyRegex, " $1").lowercase().replaceFirstChar { it.uppercase() }
                "<${MochaColorScheme.Lavender.argb}>$s <gray>➤ ${if (b) "<red>Disabled" else "<green>Enabled"}".mod()
            }

            "toggle" / "textureCopy" {
                texture.value = !texture.value
                "Texture copy mode is now: ${if (texture.value) "<green>Enabled" else "<red>Disabled"}<r>.".mod()

                if (!texture.value) return@invoke
                "Press S while hovering over a skull to copy the texture!".mod()
            }

            "simulate" / "chat" / bool("actionbar") / greedyString("message") {
                val actionBar = bool("actionbar")
                val message = string("message")

                if (actionBar) MessageEvent.ActionBar(message.parse()).post()
                else MessageEvent.Chat.Receive(message.parse()).post()

                "<gray>Simulated ($actionBar): <red>$message".mod()
            }

            "clear" / "chat" {
                MinecraftChat.instance.clearMessages(false)
            }
        }
    }
}
