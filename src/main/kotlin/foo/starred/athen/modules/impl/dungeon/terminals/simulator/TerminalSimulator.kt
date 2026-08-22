@file:Suppress("Unused")

package foo.starred.athen.modules.impl.dungeon.terminals.simulator

import foo.starred.athen.Athen
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.Category
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.override
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.SimulatorMenu
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.impl.*
import foo.starred.athen.utils.command
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.Observable

@Load
object TerminalSimulator : Module(
    "Terminal simulator",
    "Simulator terminal, terminal simulators?",
    Category.DUNGEONS
) {
    private val ipInput by config.input("Simulator server IP", "hypixelp3sim.zapto.org")
    private val _unused0 by config.information("The simulator server IP is optional. You can still do <red>\"/${Athen.modId} simulate terminals\"<r> to simulate.")
    private val pingInput = config.input("Ping", "0", "0").unique("ping")

    var ping = 0
    val s = Observable(false)
    val s0 = Observable(false)

    init {
        run {
            ping = pingInput.value.toIntOrNull() ?: return@run
        }

        pingInput.state.onChange {
            ping = it.toIntOrNull() ?: return@onChange
        }

        observable.onChange {
            SimulatorMenu.a()
            if (it) {
                "Run \"/${Athen.modId} simulate terminals ping <ping>\" to change ping!".mod()
                ConfigManager.update(configKey ?: return@onChange, false)
            }
        }

        command {
            "simulate" / "terminals" {
                SimulatorMenu.a()
            }

            "simulate" / "terminals" / "ping" / int("int") {
                ConfigManager.update("$configKey.ping", int("int").toString())
                "Ping set to ${ping}ms".mod()
            }

            "simulate" / "terminals" / "rubix" {
                RubixSimulator().a()
            }

            "simulate" / "terminals" / "color" {
                ColorSimulator().a()
            }

            "simulate" / "terminals" / "melody" {
                MelodySimulator().a()
            }

            "simulate" / "terminals" / "name" {
                NameSimulator().a()
            }

            "simulate" / "terminals" / "panes" {
                PanesSimulator().a()
            }

            "simulate" / "terminals" / "numbers" {
                NumbersSimulator().a()
            }
        }

        on<TickEvent.Client.End> {
            TickEvent.Server.post()
        }.override(s0)

        on<LocationEvent.Server.Connect> {
            s0.value = client.currentServer?.ip == ipInput
        }

        on<LocationEvent.Server.Disconnect> {
            s0.value = false
        }
    }
}