@file:Suppress("ObjectPropertyName")

package foo.starred.athen.api.dungeon.terminals

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.api.messaging.impl.MessagingAPI.dev
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.DungeonEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.TerminalSimulator
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolvers
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.gui.MinecraftGui
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.data.Observable.Companion.and
import foo.starred.kontext.api.data.Observable.Companion.or
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.scheduling.scheduler.extensions.clientTicks
import net.minecraft.network.protocol.game.*

@LoadableAthenModule
object TerminalAPI {
    private var i = 0

    val opened: Observable<Boolean> = Observable(false)

    var terminal: TerminalType? = null
        private set

    var title: String = ""
        private set

    var open: Long = 0
        private set

    var id: Int = -1
        private set

    init {
        val state = (LocationAPI.skyblock or TerminalSimulator.s) or TerminalSimulator.s0
        val state0 = state and TerminalSolvers.observable
        val state1 = state and opened

        opened.observe {
            (if (it) DungeonEvent.Terminal.Open else DungeonEvent.Terminal.Close).post()
        }

        on<PacketEvent.Process.Pre, ClientboundOpenScreenPacket> {
            val title = title.stripped()
            val type = TerminalType.get(title)?.takeIf { it.solver } ?: return@on reset()

            open = System.currentTimeMillis()
            opened.value = true
            terminal = type
            TerminalAPI.title = title
            id = containerId
            i = 0
        }.runWhen(state)

        on<PacketEvent.Process.Pre, ClientboundContainerSetSlotPacket> {
            val a = terminal ?: return@on
            val b = a.slots
            val b0 = a != TerminalType.MELODY
            if (slot !in 0 until b) return@on

            val c = MinecraftGui.container?.menu?.items?.takeIf { it.size >= b } ?: return@on
            c[slot] = item
            i++

            val d = c.subList(0, b)
            if (i < b && b0) return@on
            if (a == TerminalType.RUBIX && i > b) return@on

            DungeonEvent.Terminal.Update(d).post()
        }.runWhen(state1)

        on<PacketEvent.Process.Pre, ClientboundContainerSetContentPacket> {
            i = items.size
        }.runWhen(state1)

        on<PacketEvent.Process.Pre, ClientboundContainerClosePacket> {
            Scheduler.schedule(1.clientTicks, ::reset)
        }.runWhen(state1)

        on<PacketEvent.Send, ServerboundContainerClickPacket> {
            if (terminal == TerminalType.MELODY) return@on
            if (System.currentTimeMillis() - open >= TerminalSolvers.firstClick) return@on

            it.cancel()
        }.runWhen(state0 and opened)

        on<PacketEvent.Send, ServerboundContainerClosePacket> {
            reset()
        }.runWhen(state1)
    }

    private fun reset() {
        if (!opened.value) return

        opened.value = false
        terminal = null
        title = ""
        id = -1
        i = 0

        "TerminalAPI: reset".dev()
    }
}
