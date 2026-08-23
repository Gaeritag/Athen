@file:Suppress("EmptyRange")

package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.cascade.graphics.font.CascadeFonts
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f
import kotlin.math.abs

object RubixSolver : ITerminal(TerminalType.RUBIX) {
    private val ints = intArrayOf(12, 13, 14, 21, 22, 23, 30, 31, 32)
    //? if <= 26.1 {
    private val colors = listOf(Items.RED_STAINED_GLASS_PANE, Items.ORANGE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE)
    //? } else {
    //private val colors = listOf(Items.STAINED_GLASS_PANE.red(), Items.STAINED_GLASS_PANE.orange(), Items.STAINED_GLASS_PANE.yellow(), Items.STAINED_GLASS_PANE.green(), Items.STAINED_GLASS_PANE.blue())
    //? }

    override val int0 = 3
    override val int1 = 3

    private var last: Int? = null

    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        val font = CascadeFonts.arial

        for (c in list) {
            val x1 = (c.slot % 9 * float + x0 + 1f) * scale
            val y1 = ((c.slot / 9) * float + y0 + height + 1f) * scale

            val color = if (c.button > 0) TerminalSolver.`rubix$positive`.rgb else TerminalSolver.`rubix$negative`.rgb
            slot(graphics, x1, y1, 16f * scale, 16f * scale, color, scale, pose, scissor)

            val string = c.button.toString()
            val size = 11f * scale
            val width = font.width(string, size)
            font.extract(graphics, string, x1 + 8f * scale - width / 2, y1 + 3f * scale, Mocha.Text.rgba, false, size)
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