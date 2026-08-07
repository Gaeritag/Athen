package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.athen.utils.nvg.NVGRenderer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object MelodySolver : ITerminal(TerminalType.MELODY) {
    private val slots = setOf(16, 25, 34, 43)

    override val float: Float
        get() = 16f + TerminalSolver.`ui$melodyGap`

    var button: Int? = null
    var current: Int? = null
    var correct: Int? = null

    fun click(int: Int) {
        if (int !in 1..4) return
        click(16 + (int - 1) * 9, 0)
    }

    override fun render(ox: Float, oy: Float, headerH: Float, uiScale: Float) {
        val button = button ?: return
        val current = current ?: return
        val correct = correct ?: return
        val sp = float

        val row = button + 1
        val rowY = (row * sp + oy + headerH + 1f) * uiScale
        val rowX = (sp + ox + 1f) * uiScale
        val size = 16f * uiScale
        val spScaled = sp * uiScale

        for (i in 0 until 5) {
            val x = rowX + i * spScaled
            when (i) {
                current -> NVGRenderer.drawOutlinedRectangle(x, rowY, size, size, TerminalSolver.`melody$fill`.rgb, if (i == correct) TerminalSolver.`melody$correct`.rgb else TerminalSolver.`melody$wrong`.rgb, uiScale, TerminalSolver.`ui$slots$roundness` * uiScale)
                correct -> NVGRenderer.drawHollowRectangle(x, rowY, size, size, uiScale, TerminalSolver.`melody$correct`.rgb, TerminalSolver.`ui$slots$roundness` * uiScale)
                else -> NVGRenderer.drawHollowRectangle(x, rowY, size, size, uiScale, TerminalSolver.`melody$wrong`.rgb, TerminalSolver.`ui$slots$roundness` * uiScale)
            }
        }

        val rows = terminalType.slots / 9
        val buttonSlot = button * 9 + 16

        for (slot in 0 until terminalType.slots) {
            val r = slot / 9
            val c = slot % 9
            if (r == 0 || r == rows - 1 || c == 0 || c == 8) continue

            val x = (c * sp + ox + 1f) * uiScale
            val y = (r * sp + oy + headerH + 1f) * uiScale

            when {
                slot == buttonSlot -> drawSlot(x, y, size, size, TerminalSolver.`melody$correct`.rgb, uiScale)
                slot in slots -> drawSlot(x, y, size, size, TerminalSolver.`melody$wrong`.rgb, uiScale)
                r in 1..4 && r != row -> {
                    if (c !in 1..5) continue
                    drawSlot(x, y, size, size, TerminalSolver.`melody$other`.rgb, uiScale)
                }
            }
        }
    }

    override fun forSlot(slot: Int): Click? {
        return (slot in slots).also { if (it) click(slot, 0) }.let { null }
    }

    override fun valid(click: Click): Boolean {
        return false
    }

    override fun onClose() {
        button = null
        correct = null
        current = null
        super.onClose()
    }

    override fun compute(items: List<ItemStack>) {
        var a = -1
        var b = -1

        for (i in items.indices) {
            val s = items[i].item
            if (a == -1 && s == Items.LIME_STAINED_GLASS_PANE) a = i
            if (b == -1 && s == Items.MAGENTA_STAINED_GLASS_PANE) b = i
            if (a != -1 && b != -1) break
        }

        if (a == -1) return
        if (b != -1) correct = b - 1

        button = a / 9 - 1
        current = a % 9 - 1
    }
}