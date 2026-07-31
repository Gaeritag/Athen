@file:Suppress("ObjectPropertyName")

package foo.starred.athen.api.dungeon.terminals

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.protocol.game.*
import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.dungeon.DungeonAPI
import foo.starred.athen.events.DungeonEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.handlers.Chronos
import foo.starred.athen.handlers.Typo.devMessage
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.TerminalSimulator
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.snowbird.api.client
import foo.starred.snowbird.handlers.Observable
import foo.starred.snowbird.handlers.Observable.Companion.and
import foo.starred.snowbird.handlers.Observable.Companion.or
import foo.starred.snowbird.handlers.time.client
import foo.starred.snowbird.utils.stripped

@Priority
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
        val state = (DungeonAPI.F7Phase.map { it == 3 } or TerminalSimulator.s) or TerminalSimulator.s0
        val state0 = state and TerminalSolver.observable
        val state1 = state and opened

        opened.onChange {
            (if (it) DungeonEvent.Terminal.Open else DungeonEvent.Terminal.Close).post()
        }

        on<PacketEvent.Process.Pre, ClientboundOpenScreenPacket> {
            val title = title.stripped()
            val type = TerminalType.get(title)?.takeIf { it.solver } ?: return@on reset()

            if (!opened.value) open = System.currentTimeMillis()
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

            if (containerId != id) return@on
            if (slot !in 0 until b) return@on

            val c = (client.screen as? AbstractContainerScreen<*>)?.menu?.items?.takeIf { it.size >= b } ?: return@on
            c[slot] = item
            i++

            val d = c.subList(0, b)
            if (i < b && b0) return@on

            DungeonEvent.Terminal.Update(d).post()
        }.runWhen(state1)

        on<PacketEvent.Process.Pre, ClientboundContainerSetContentPacket> {
            if (containerId != id) return@on
            i = items.size
        }.runWhen(state1)

        on<PacketEvent.Process.Pre, ClientboundContainerClosePacket> {
            Chronos.schedule(1.client, ::reset)
        }.runWhen(state1)

        on<PacketEvent.Send, ServerboundContainerClickPacket> {
            if (terminal == TerminalType.MELODY) return@on
            if (containerId != id) return@on it.cancel()
            if (System.currentTimeMillis() - open >= TerminalSolver.fcDelay) return@on

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

        "TerminalAPI: reset".devMessage()
    }
}