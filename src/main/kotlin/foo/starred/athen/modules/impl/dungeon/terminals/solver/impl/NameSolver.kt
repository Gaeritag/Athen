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

object NameSolver : ITerminal(TerminalType.NAME) {
    private val i = mutableSetOf<Int>()

    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        for (c in list) {
            val sx = (c.slot % 9 * float + ox + 1f) * uiScale
            val sy = ((c.slot / 9) * float + oy + headerH + 1f) * uiScale
            drawSlot(sx, sy, 16f * uiScale, 16f * uiScale, TerminalSolver.`names$correct`.rgb, uiScale)
        }
    }

    override fun forSlot(slot: Int): Click? {
        return list.find { it.slot == slot }
    }

    override fun valid(click: Click): Boolean {
        return list.any { it.button == click.button }
    }

    override fun click(slot: Int, button: Int) {
        i.add(slot)
        super.click(slot, button)
    }

    override fun onClose() {
        i.clear()
        super.onClose()
    }

    override fun onResync() {
        i.clear()
    }

    override fun compute(items: List<ItemStack>) {
        list.clear()

        val match = TerminalType.NAME.regex.matchEntire(TerminalAPI.title)
        val targetLetter = match?.groupValues?.get(1)?.lowercase() ?: return

        for (i0 in items.indices) {
            val s = items[i0]
            if (i0 in i) continue
            if (s.isEmpty) continue
            if (s.item == Items.BLACK_STAINED_GLASS_PANE) continue
            if (s.glint()) continue
            if (!s.hoverName.stripped().lowercase().startsWith(targetLetter, true)) continue

            list.add(Click(i0, 0))
        }
    }
}