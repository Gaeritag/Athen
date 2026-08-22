package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.font.CascadeFonts
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f

object NumbersSolver : ITerminal(TerminalType.NUMBERS) {
    private val counts = mutableMapOf<Int, Int>()

    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        val font = CascadeFonts.arial
        for ((i, c) in list.withIndex()) {
            if (i > 2) break

            val x1 = (c.slot % 9 * float + x0 + 1f) * scale
            val y1 = ((c.slot / 9) * float + y0 + height + 1f) * scale
            val color = i.get() ?: continue

            slot(graphics, x1, y1, 16f * scale, 16f * scale, color, scale, pose, scissor)

            if (!TerminalSolver.`ui$numbers$showText`) continue
            val a = counts[c.slot]?.toString() ?: continue
            val b = 11f * scale
            val d = font.width(a, b)
            font.extract(graphics, a, x1 + 8f * scale - d / 2, y1 + 3f * scale, Mocha.Text.rgba, false, b)
        }
    }

    override fun forSlot(slot: Int): Click? {
        return list.firstOrNull()?.takeIf { it.slot == slot }
    }

    override fun valid(click: Click): Boolean {
        val a = list.firstOrNull()
        return a != null && a.slot == click.slot
    }

    override fun onClose() {
        counts.clear()
        super.onClose()
    }

    override fun compute(items: List<ItemStack>) {
        list.clear()

        val a = items.indices.sortedBy { items[it].count }
        for (b in a) {
            val c = items[b]
            //~ if >= 26.2 'Items.RED_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.red()'
            if (c.item != Items.RED_STAINED_GLASS_PANE) continue

            counts[b] = c.count
            list.add(Click(b, 0))
        }
    }

    private fun Int.get(): Int? = when (this) {
        0 -> TerminalSolver.`numbers$first`.rgb
        1 -> TerminalSolver.`numbers$second`.rgb
        2 -> TerminalSolver.`numbers$third`.rgb
        else -> null
    }
}