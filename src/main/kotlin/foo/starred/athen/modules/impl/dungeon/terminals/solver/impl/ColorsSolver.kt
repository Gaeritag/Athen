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

object ColorsSolver : ITerminal(TerminalType.COLORS) {
    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        for (c in list) {
            val x1 = (c.slot % 9 * float + x0 + 1f) * scale
            val y1 = ((c.slot / 9) * float + y0 + height + 1f) * scale
            slot(graphics, x1, y1, 16f * scale, 16f * scale, TerminalSolver.`colors$correct`.rgb, scale, pose, scissor)
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
            //~ if >= 26.2 'Items.BLACK_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.black()'
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
            //~ if >= 26.2 'Items.WHITE_WOOL' -> 'Items.WOOL.white()'
            "white" -> item == Items.BONE_MEAL || item == Items.WHITE_WOOL
            "green" -> item == Items.CACTUS
            "red" -> item == Items.POPPY
            "yellow" -> item == Items.DANDELION
            else -> false
        }
    }
}