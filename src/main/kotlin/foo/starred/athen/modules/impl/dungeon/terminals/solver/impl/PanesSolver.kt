package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal

object PanesSolver : ITerminal(TerminalType.PANES) {
    override val int0 = 5
    override val int1 = 2

    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        for (c in list) {
            val sx = (c.slot % 9 * float + ox + 1f) * uiScale
            val sy = ((c.slot / 9) * float + oy + headerH + 1f) * uiScale
            drawSlot(sx, sy, 16f * uiScale, 16f * uiScale, TerminalSolver.`panes$correct`.rgb, uiScale)
        }
    }

    override fun forSlot(slot: Int): Click? {
        return list.find { it.slot == slot }
    }

    override fun valid(click: Click): Boolean {
        return list.any { it.button == click.button }
    }

    override fun compute(items: List<ItemStack>) {
        list.clear()

        for (i in items.indices) {
            if (items[i].item != Items.RED_STAINED_GLASS_PANE) continue
            list.add(Click(i, 0))
        }
    }
}