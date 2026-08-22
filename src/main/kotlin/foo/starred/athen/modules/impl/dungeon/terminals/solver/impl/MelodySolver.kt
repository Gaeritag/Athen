package foo.starred.athen.modules.impl.dungeon.terminals.solver.impl

import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.Click
import foo.starred.athen.modules.impl.dungeon.terminals.solver.base.ITerminal
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.states.RoundedRectangleRenderState
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f

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

    override fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        val button = button ?: return
        val current = current ?: return
        val correct = correct ?: return
        val float = float

        val row = button + 1
        val x1 = (float + x0 + 1f) * scale
        val y1 = (row * float + y0 + height + 1f) * scale
        val size = 16f * scale
        val sp = float * scale
        val radius = RoundedRectangleRadius.of(TerminalSolver.`ui$slots$roundness` * scale)

        for (i in 0 until 5) {
            val x = x1 + i * sp
            val color = if (i == correct) TerminalSolver.`melody$correct`.rgb else TerminalSolver.`melody$wrong`.rgb

            when (i) {
                current -> {
                    RoundedRectangleRenderState.extract(graphics, x, y1, size, size, TerminalSolver.`melody$fill`.rgb, radius, pose = pose, scissor = scissor)
                    RoundedRectangleRenderState.extract(graphics, x, y1, size, size, color, radius, outline = scale, pose = pose, scissor = scissor)
                }

                correct -> {
                    RoundedRectangleRenderState.extract(graphics, x, y1, size, size, TerminalSolver.`melody$correct`.rgb, radius, outline = scale, pose = pose, scissor = scissor)
                }

                else -> {
                    RoundedRectangleRenderState.extract(graphics, x, y1, size, size, TerminalSolver.`melody$wrong`.rgb, radius, outline = scale, pose = pose, scissor = scissor)
                }
            }
        }

        val rows = terminalType.slots / 9
        val buttonSlot = button * 9 + 16

        for (slot in 0 until terminalType.slots) {
            val r = slot / 9
            val c = slot % 9
            if (r == 0 || r == rows - 1 || c == 0 || c == 8) continue

            val x = (c * float + x0 + 1f) * scale
            val y = (r * float + y0 + height + 1f) * scale

            when {
                slot == buttonSlot -> slot(graphics, x, y, size, size, TerminalSolver.`melody$correct`.rgb, scale, pose, scissor)
                slot in slots -> slot(graphics, x, y, size, size, TerminalSolver.`melody$wrong`.rgb, scale, pose, scissor)
                r in 1..4 && r != row -> {
                    if (c !in 1..5) continue
                    slot(graphics, x, y, size, size, TerminalSolver.`melody$other`.rgb, scale, pose, scissor)
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
            //~ if >= 26.2 'Items.LIME_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.lime()'
            if (a == -1 && s == Items.LIME_STAINED_GLASS_PANE) a = i
            //~ if >= 26.2 'Items.MAGENTA_STAINED_GLASS_PANE' -> 'Items.STAINED_GLASS_PANE.magenta()'
            if (b == -1 && s == Items.MAGENTA_STAINED_GLASS_PANE) b = i
            if (a != -1 && b != -1) break
        }

        if (a == -1) return
        if (b != -1) correct = b - 1

        button = a / 9 - 1
        current = a % 9 - 1
    }
}