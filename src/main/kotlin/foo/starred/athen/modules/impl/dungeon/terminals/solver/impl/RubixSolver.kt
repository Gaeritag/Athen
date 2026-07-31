@file:Suppress("EmptyRange")

package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.nvg.NVGRenderer
import kotlin.math.abs

object RubixSolver : ITerminal(TerminalType.RUBIX) {
    private val ints = intArrayOf(12, 13, 14, 21, 22, 23, 30, 31, 32)
    private val colors = listOf(Items.RED_STAINED_GLASS_PANE, Items.ORANGE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE)

    override val int0 = 3
    override val int1 = 3

    private var last: Int? = null

    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        for (c in list) {
            val sx = (c.slot % 9 * float + ox + 1f) * uiScale
            val sy = ((c.slot / 9) * float + oy + headerH + 1f) * uiScale

            val color = if (c.button > 0) TerminalSolver.`rubix$positive`.rgb else TerminalSolver.`rubix$negative`.rgb
            drawSlot(sx, sy, 16f * uiScale, 16f * uiScale, color, uiScale)

            val btnStr = c.button.toString()
            val btnWidth = NVGRenderer.getTextWidth(btnStr, 11f * uiScale, NVGRenderer.defaultFont)
            NVGRenderer.drawText(btnStr, sx + 8f * uiScale - btnWidth / 2, sy + 3f * uiScale, 11f * uiScale, Mocha.Text.rgba)
        }
    }

    override fun forSlot(slot: Int): Click? {
        return list.find { it.slot == slot }?.button?.let { Click(slot, if (it > 0) 0 else 1) }
    }

    override fun valid(click: Click): Boolean {
        val sol = list.find { it.slot == click.slot }
        return sol != null && ((sol.button > 0 && click.button == 0) || (sol.button < 0 && click.button == 1))
    }

    override fun onClose() {
        last = null
        super.onClose()
    }

    override fun compute(items: List<ItemStack>) {
        list.clear()

        val allowed = BooleanArray(54)
        for (s in ints) allowed[s] = true

        val slots = IntArray(9)
        val ides = IntArray(9)
        var count = 0

        for (i in items.indices) {
            val s = items[i]

            if (i >= allowed.size) continue
            if (!allowed[i]) continue

            val idx = colors.indexOf(s.item).takeIf { it != -1 } ?: continue
            slots[count] = i
            ides[count] = idx
            count++
        }

        val costs = IntArray(5)
        for (t in 0 until 5) {
            var c = 0

            for (i in 0 until count) {
                val d = abs(t - ides[i])
                c += if (d > 2) 5 - d else d
            }

            costs[t] = c
        }

        var best = 0
        for (i in 1 until 5) if (costs[i] < costs[best]) best = i

        val o = last?.takeIf { costs[it] != 0 } ?: best.also { last = it }
        for (i in 0 until count) {
            val idx = ides[i]
            if (idx == o) continue

            var diff = o - idx
            if (diff > 2) diff -= 5 else if (diff < -2) diff += 5

            list.add(Click(slots[i], diff))
        }
    }
}