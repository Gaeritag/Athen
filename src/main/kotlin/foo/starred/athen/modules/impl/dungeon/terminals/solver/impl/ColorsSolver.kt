package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import foo.starred.athen.api.dungeon.terminals.TerminalAPI
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.utils.glint
import foo.starred.snowbird.utils.stripped

object ColorsSolver : ITerminal(TerminalType.COLORS) {
    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        for (c in list) {
            val sx = (c.slot % 9 * float + ox + 1f) * uiScale
            val sy = ((c.slot / 9) * float + oy + headerH + 1f) * uiScale
            drawSlot(sx, sy, 16f * uiScale, 16f * uiScale, TerminalSolver.`colors$correct`.rgb, uiScale)
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

        val str = TerminalType.COLORS.regex.matchEntire(TerminalAPI.title)?.groupValues?.get(1)?.replace("SILVER", "LIGHT GRAY")?.lowercase() ?: return
        for (i in items.indices) {
            val s = items[i]
            if (s.item == Items.BLACK_STAINED_GLASS_PANE) continue
            if (s.glint()) continue
            if (!s.matches(str)) continue

            list.add(Click(i, 0))
        }
    }

    private fun ItemStack.matches(str: String): Boolean {
        return item.getName(item.defaultInstance).stripped().lowercase().startsWith(str) || hoverName.stripped().lowercase().startsWith(str) || when (str) {
            "black" -> item == Items.INK_SAC
            "blue" -> item == Items.LAPIS_LAZULI
            "brown" -> item == Items.COCOA_BEANS
            "white" -> item == Items.BONE_MEAL || item == Items.WHITE_WOOL
            "green" -> item == Items.CACTUS
            "red" -> item == Items.POPPY
            "yellow" -> item == Items.DANDELION
            else -> false
        }
    }
}