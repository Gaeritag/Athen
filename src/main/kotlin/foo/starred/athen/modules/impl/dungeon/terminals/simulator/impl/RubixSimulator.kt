package foo.starred.athen.modules.impl.dungeon.terminals.simulator.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.ITerminalSim
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.SimulatorMenu
import foo.starred.snowbird.api.EMPTY_COMPONENT
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class RubixSimulator : ITerminalSim(TerminalType.RUBIX) {
    //? if <= 26.1 {
    private val items = listOf(Items.ORANGE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE, Items.RED_STAINED_GLASS_PANE)
    //?} else {
    //private val items = listOf(Items.STAINED_GLASS_PANE.orange(), Items.STAINED_GLASS_PANE.yellow(), Items.STAINED_GLASS_PANE.green(), Items.STAINED_GLASS_PANE.blue(), Items.STAINED_GLASS_PANE.red())
    //?}

    override fun s(): Map<Int, ItemStack> = buildMap {
        for (row in 1..3) for (col in 3..5) put(row * 9 + col, items.random().pane())
    }

    override fun click(slot: Slot, button: Int) {
        val item = slot.item.item
        val i = items.indexOfFirst { it == item }.takeIf { it != -1 } ?: return
        val n = if (button == 1) (i - 1 + items.size) % items.size else (i + 1) % items.size

        mapOf(slot.containerSlot to items[n].pane()).a()
        if (c()) SimulatorMenu.a()
    }

    private fun c(): Boolean {
        var r: Item? = null

        for (s in slots) {
            //~ if >= 26.2 'Items.BLACK_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.black()'
            val it = s.item.item.takeIf { it != Items.BLACK_STAINED_GLASS_PANE } ?: continue

            if (r == null) r = it
            else if (it != r) return false
        }

        return r != null
    }

    private fun Item.pane(): ItemStack =
        ItemStack(this).apply { set(DataComponents.CUSTOM_NAME, EMPTY_COMPONENT) }
}