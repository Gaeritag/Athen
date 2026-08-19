package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f

object PanesSolver : ITerminal(TerminalType.PANES) {
    override val int0 = 5
    override val int1 = 2

    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        for (c in list) {
            val x1 = (c.slot % 9 * float + x0 + 1f) * scale
            val y1 = ((c.slot / 9) * float + y0 + height + 1f) * scale
            slot(graphics, x1, y1, 16f * scale, 16f * scale, TerminalSolver.`panes$correct`.rgb, scale, pose, scissor)
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
            //~ if >= 26.2 'Items.RED_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.red()'
            if (items[i].item != Items.RED_STAINED_GLASS_PANE) continue
            list.add(Click(i, 0))
        }
    }
}