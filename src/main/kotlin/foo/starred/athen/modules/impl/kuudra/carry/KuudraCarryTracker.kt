@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.kuudra.carry

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraTier
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.network.http.WebAPI.request
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.kuudra.carry.KuudraCarryStateTracker.tracked
import foo.starred.athen.utils.command
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.chat.generic.MinecraftChat
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.abstraction.tablist.MinecraftTabList
import foo.starred.kontext.abstraction.text.measurer.MinecraftFontMeasurer
import foo.starred.kontext.abstraction.text.renderer.MinecraftFontRenderer
import foo.starred.kontext.api.entity.extension.renderAABB
import foo.starred.kontext.api.format.extension.component.formattedText
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.kontext.api.network.data.HttpRequest
import foo.starred.parallax.api.primitives.ParallaxBox

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object KuudraCarryTracker : Module(
    "Kuudra carry tracker",
    "Track kuudra carries and display progress.",
    ConfigCategory.KUUDRA
) {
    private val announceInParty by config.switch("Announce in party", true)
    private val showStartMessage by config.switch("Show start message", true)

    private val _webhook by config.group("Discord webhook")
    private val webhook by _webhook.switch("Send to webhook")
    private val webhookEach by _webhook.switch("Send on each kill", true)
    private val webhookUrl by _webhook.input("Webhook URL").description("Requires you to add your own webhook URL!")

    private val highlights by config.group("Highlights")
    private val highlightPlayer by highlights.switch("Highlight player", true)
    private val playerColor by highlights.colorPicker("Player color", MochaColorScheme.Blue.argb)
    private val playerLineWidth by highlights.slider("Player line width", 2f, 0f, 10f)

    private val hud by config.hud("Kuudra carry display") {
        val example = listOf("§f§lKuudra Carries:", "§7> §bExample §8[§7Infernal§8]§f: §b3§f/§b10 §7(5m 30s | 12/hr)").formattedText()

        constrain {
            MinecraftFontMeasurer.constrain(example)
        }

        preview {
            MinecraftFontRenderer.extract(graphics, example, 0, 0)
        }

        render {
            MinecraftFontRenderer.extract(graphics, display.value ?: return@render, 0, 0)
        }
    }

    private val `hud$kuudra` by config.switch("Only in Kuudra", true)

    private val display = Ticking {
        if (tracked.isEmpty()) return@Ticking null
        if (`hud$kuudra` && !PresetSkyBlockIsland.KUUDRA.active) return@Ticking null

        buildString {
            append("§f§lKuudra Carries:")
            for (i in tracked.values) append("\n${i.str()}")
        }.split("\n").formattedText()
    }

    init {
        command {
            "kcarry" / "add" / word("player").suggests { MinecraftTabList.players.map { it.profile.name } } / int("amount", 1).suggests { listOf("1", "5", "10", "20") } / word("tier") {
                val player = string("player")
                val amount = int("amount")
                val tier0 = string("tier")

                val tier = KuudraTier.entries.find { it.name.equals(tier0, true) } ?: return@word "Invalid tier. Use: basic, hot, burning, fiery, infernal, or t1-t5.".mod()

                KuudraCarryStateTracker.addCarry(player, amount, tier)
            }.suggests { KuudraTier.entries.map { it.name.lowercase() } }

            "kcarry" / "remove" / word("player") {
                KuudraCarryStateTracker.removeCarry(string("player"))
            }.suggests { tracked.keys }

            "kcarry" / "list" {
                KuudraCarryStateTracker.listCarries()
            }

            "kcarry" / "list" / "clear" {
                KuudraCarryStateTracker.clearCarries()
            }

            "kcarry" / "history" {
                KuudraCarryStateTracker.displayHistory(1)
            }

            "kcarry" / "history" / int("page", 1) {
                KuudraCarryStateTracker.displayHistory(int("page"))
            }.suggests { listOf("1", "2", "3", "4", "5") }

            "kcarry" / "help" {
                showHelp()
            }

            "kcarry" / "gui" {
                KuudraCarryGUI.open()
            }

            "kcarry" {
                KuudraCarryGUI.open()
            }
        }

        on<KuudraEvent.Start> {
            val tier = KuudraAPI.tier ?: return@on

            for (teammate in KuudraAPI.teammates) {
                val carry = tracked[teammate.name] ?: continue
                if (carry.tier != tier) continue

                if (showStartMessage) "Kuudra started for <aqua>${teammate.name}<gray> [${tier.str}]".mod()
            }
        }

        on<KuudraEvent.End.Success> {
            val tier = KuudraAPI.tier ?: return@on

            for (teammate in KuudraAPI.teammates) {
                val carry = tracked[teammate.name] ?: continue
                if (carry.tier != tier) continue

                val result = carry.onCompletion()

                "Completed run for <aqua>${teammate.name}".mod()

                if (announceInParty) {
                    MinecraftCommands.client("pc ${teammate.name}: ${result.current}/${result.total}")
                }

                if (webhookEach && webhook) {
                    webhookUrl.request(HttpRequest.POST) {
                        body(mapOf("content" to "Completed ${result.amount}/${result.total} ${tier.str} carries for ${teammate.name}"))
                    }
                }

                if (result.completed) {
                    val time = result.totalTime.parseDuration()
                    "<${MochaColorScheme.Green.argb}>Completed carries for <aqua>${teammate.name} <gray>[${tier.str}] <r>in <yellow>$time".mod()

                    if (webhook) {
                        webhookUrl.request(HttpRequest.POST) {
                            body(mapOf("content" to "Completed ${result.amount}x ${tier.str} carries for ${teammate.name} ($time)"))
                        }
                    }

                    KuudraCarryStateTracker.add(teammate.name, result.amount, carry.type)
                    tracked.remove(teammate.name)
                }

                KuudraCarryStateTracker.persist()
            }
        }

        on<WorldRenderEvent.Extract> {
            if (!highlightPlayer) return@on
            if (tracked.isEmpty()) return@on
            if (!KuudraAPI.inRun) return@on

            for (teammate in KuudraAPI.teammates) {
                if (teammate.name !in tracked) continue
                val e = teammate.entity ?: continue
                ParallaxBox.frame(e.renderAABB(), playerColor, playerLineWidth, false)
            }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)
    }

    private fun showHelp() {
        val commands = listOf(
            "/athen kcarry" to "Open the kuudra carry tracker GUI",
            "/athen kcarry add <player> <amount> <tier>" to "Add kuudra carries to track",
            "/athen kcarry remove <player>" to "Remove a tracked player",
            "/athen kcarry list" to "List players being tracked",
            "/athen kcarry list clear" to "Clear the active list",
            "/athen kcarry history [page=1]" to "Show tracked history"
        )

        val divider = ("§8§m" + MinecraftChat.repeat("-")).literal()

        MinecraftMessages.display(divider)
        MinecraftMessages.display(MinecraftChat.center("§bAthen Kuudra Carry Commands"))
        MinecraftMessages.display(divider)

        for ((c, d) in commands) MinecraftMessages.display("  <${MochaColorScheme.Green.argb}>$c <dark_gray>- <gray>$d")

        MinecraftMessages.display(divider)
    }
}
