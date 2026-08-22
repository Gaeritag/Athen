package foo.starred.athen.modules.impl.dungeon.terminals.simulator.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.ITerminalSim
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.SimulatorMenu
import foo.starred.snowbird.api.EMPTY_COMPONENT
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.random.Random

class PanesSimulator : ITerminalSim(TerminalType.PANES) {
    override fun s(): Map<Int, ItemStack> = buildMap {
        for (row in 1..3) for (col in 2..6) put(row * 9 + col, pane(Random.nextDouble() < 0.25))
    }

    override fun click(slot: Slot, button: Int) {
        //~ if >= 26.2 'Items.LIME_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.lime()'
        mapOf(slot.containerSlot to pane(slot.item.item != Items.LIME_STAINED_GLASS_PANE)).a()
        if (c()) SimulatorMenu.a()
    }

    private fun c(): Boolean {
        for (s in slots) {
            val it = s.item.item
            //~ if >= 26.2 'it != Items.BLACK_STAINED_GLASS_PANE && it == Items.RED_STAINED_GLASS_PANE' -> 'it != Items.STAINED_GLASS_PANE.black() && it == Items.STAINED_GLASS_PANE.red()'
            if (it != Items.BLACK_STAINED_GLASS_PANE && it == Items.RED_STAINED_GLASS_PANE) return false
        }

        return true
    }

    private fun pane(bool: Boolean): ItemStack =
        //~ if >= 26.2 'Items.LIME_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.lime() else Items.STAINED_GLASS_PANE.red()'
        ItemStack(if (bool) Items.LIME_STAINED_GLASS_PANE else Items.RED_STAINED_GLASS_PANE).apply {
            set(DataComponents.CUSTOM_NAME, EMPTY_COMPONENT)
        }
}