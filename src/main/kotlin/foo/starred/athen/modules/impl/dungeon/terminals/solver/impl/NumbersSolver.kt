package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.nvg.NVGRenderer

object NumbersSolver : ITerminal(TerminalType.NUMBERS) {
    private val counts = mutableMapOf<Int, Int>()

    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        for ((i, c) in list.withIndex()) {
            if (i > 2) break

            val sx = (c.slot % 9 * float + ox + 1f) * uiScale
            val sy = ((c.slot / 9) * float + oy + headerH + 1f) * uiScale
            val color = i.get() ?: continue

            drawSlot(sx, sy, 16f * uiScale, 16f * uiScale, color, uiScale)

            if (!TerminalSolver.`ui$numbers$showText`) continue
            val a = counts[c.slot]?.toString() ?: continue
            val b = NVGRenderer.getTextWidth(a, 11f * uiScale, NVGRenderer.defaultFont)
            NVGRenderer.drawText(a, sx + 8f * uiScale - b / 2, sy + 3f * uiScale, 11f * uiScale, Mocha.Text.rgba)
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