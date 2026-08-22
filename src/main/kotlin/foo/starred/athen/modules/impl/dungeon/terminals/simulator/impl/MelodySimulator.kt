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

//? if >= 26.2
//import net.minecraft.world.level.block.Blocks

class MelodySimulator : ITerminalSim(TerminalType.MELODY) {
    //? if <= 26.1 {
    private val magentaPane = Items.MAGENTA_STAINED_GLASS_PANE.pane()
    private val greenPane = Items.LIME_STAINED_GLASS_PANE.pane()
    private val redPane = Items.RED_STAINED_GLASS_PANE.pane()
    private val whitePane = Items.WHITE_STAINED_GLASS_PANE.pane()
    private val redClay = Items.RED_TERRACOTTA.pane()
    private val greenClay = Items.LIME_TERRACOTTA.pane()
    //?} else {
    /*private val magentaPane = Items.STAINED_GLASS_PANE.magenta().pane()
    private val greenPane = Items.STAINED_GLASS_PANE.lime().pane()
    private val redPane = Items.STAINED_GLASS_PANE.red().pane()
    private val whitePane = Items.STAINED_GLASS_PANE.white().pane()
    private val redClay = Blocks.DYED_TERRACOTTA.red().asItem().pane()
    private val greenClay = Blocks.DYED_TERRACOTTA.lime().asItem().pane()
    *///?}


    private var magentaColumn = (1..5).random()
    private var limeColumn = 1
    private var limeDirection = 1
    private var currentRow = 1
    private var counter = 0

    override fun s(): Map<Int, ItemStack> = emptyMap<Int, ItemStack>().also { g() }

    override fun containerTick() {
        super.containerTick()
        if (counter++ % 10 != 0) return

        limeColumn += limeDirection
        if (limeColumn == 1 || limeColumn == 5) limeDirection *= -1

        g()
    }

    override fun click(slot: Slot, button: Int) {
        if (slot.containerSlot % 9 != 7) return
        if (slot.containerSlot / 9 != currentRow) return
        if (limeColumn != magentaColumn) return

        magentaColumn = (1..5).random()
        currentRow++

        g()

        if (currentRow >= 5) SimulatorMenu.a()
    }

    private fun g() {
        val updates = mutableMapOf<Int, ItemStack>()
        for (s in slots) updates[s.containerSlot] = s.a().also(s::set)
        updates.a()
    }

    private fun Slot.a(): ItemStack {
        val row = containerSlot / 9
        val col = containerSlot % 9

        return when (row) {
            currentRow -> when (col) {
                limeColumn -> greenPane
                in 1..5 -> redPane
                7 -> greenClay
                else -> pane
            }

            in 1..4 -> when (col) {
                in 1..5 -> whitePane
                7 -> redClay
                else -> pane
            }

            else -> when (col) {
                magentaColumn -> magentaPane
                else -> pane
            }
        }
    }

    private fun Item.pane() =
        ItemStack(this).apply { set(DataComponents.CUSTOM_NAME, EMPTY_COMPONENT) }
}