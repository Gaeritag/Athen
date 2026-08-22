package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalAPI
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.utils.glint
import foo.starred.snowbird.utils.stripped
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f

object NameSolver : ITerminal(TerminalType.NAME) {
    private val i = mutableSetOf<Int>()

    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        for (c in list) {
            val x1 = (c.slot % 9 * float + x0 + 1f) * scale
            val y1 = ((c.slot / 9) * float + y0 + height + 1f) * scale
            slot(graphics, x1, y1, 16f * scale, 16f * scale, TerminalSolver.`names$correct`.rgb, scale, pose, scissor)
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
            //~ if >= 26.2 'Items.BLACK_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.black()'
            if (s.item == Items.BLACK_STAINED_GLASS_PANE) continue
            if (s.glint()) continue
            if (!s.hoverName.stripped().lowercase().startsWith(targetLetter, true)) continue

            list.add(Click(i0, 0))
        }
    }
}