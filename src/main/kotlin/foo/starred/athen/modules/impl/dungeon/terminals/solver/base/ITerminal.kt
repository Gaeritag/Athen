package foo.starred.athen.modules.impl.dungeon.terminals.solver.base

import foo.starred.athen.api.dungeon.terminals.TerminalAPI
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.TerminalSimulator
import foo.starred.athen.modules.impl.dungeon.terminals.simulator.base.ITerminalSim
import foo.starred.athen.modules.impl.dungeon.terminals.solver.TerminalSolver
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.guiClick
import foo.starred.cascade.font.CascadeFonts
import foo.starred.cascade.primitives.data.roundedrectangle.RoundedRectangleRadius
import foo.starred.cascade.primitives.states.RoundedRectangleRenderState
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f
import java.util.concurrent.CopyOnWriteArrayList

abstract class ITerminal(val terminalType: TerminalType) {
    protected val list = CopyOnWriteArrayList<Click>()
    protected open val int0: Int = 7
    protected open val int1: Int = 1
    protected open val float: Float
        get() = 16f + TerminalSolver.`ui$gap`

    var clicked: Boolean = false

    open fun onOpen() {
        clicked = false
    }

    open fun onClose() {
        clicked = false
    }

    open fun onResync() {
    }

    protected abstract fun compute(items: List<ItemStack>)

    protected abstract fun render(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, height: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?)

    protected abstract fun valid(click: Click): Boolean

    protected abstract fun forSlot(slot: Int): Click?

    fun main(graphics: GuiGraphicsExtractor) {
        val sp = float
        val pad = TerminalSolver.`ui$padding`
        val scale = TerminalSolver.`ui$scale`
        val w = client.window.guiScaledWidth.toFloat() / scale
        val h = client.window.guiScaledHeight.toFloat() / scale

        val gridW = int0 * sp + 2 * pad
        val gridH = (terminalType.slots / 9 - 2) * sp + 2 * pad
        val headerH = if (TerminalSolver.`ui$hideHeader`) 0f else 20f
        val padding = if (TerminalSolver.`ui$hideHeader`) 0f else 6f
        val totalH = gridH + headerH + padding

        val x0 = w / 2 - gridW / 2
        val y0 = h / 2 - totalH / 2

        val inset = (sp - 16f) / 2f

        val pose = Matrix3x2f(graphics.pose())
        val scissor = graphics.scissorStack.peek()
        val radius = RoundedRectangleRadius.of(TerminalSolver.`ui$roundness` * scale)

        val x1 = x0 * scale
        val y1 = (y0 + headerH + padding) * scale
        val w1 = gridW * scale
        val h1 = gridH * scale

        RoundedRectangleRenderState.extract(graphics, x1, y1, w1, h1, TerminalSolver.`ui$bg`.rgb, radius, pose = pose, scissor = scissor)
        RoundedRectangleRenderState.extract(graphics, x1, y1, w1, h1, TerminalSolver.`ui$border`.rgb, radius, maxOf(1f, scale / 2f), pose = pose, scissor = scissor)

        main(graphics, x0, y0, gridW, headerH, scale, pose, scissor)
        render(graphics, x0 - int1 * sp + pad + inset - 1f, y0 + headerH + padding - sp + pad + inset - 1f, 0f, scale, pose, scissor)
    }

    fun click(mx: Float, my: Float, width: Float, height: Float, mouseButton: Int) {
        val sp = float
        val pad = TerminalSolver.`ui$padding`
        val slots = terminalType.slots
        val gridW = int0 * sp + 2 * pad
        val gridH = (terminalType.slots / 9 - 2) * sp + 2 * pad
        val headerH = if (TerminalSolver.`ui$hideHeader`) 0f else 20f
        val padding = if (TerminalSolver.`ui$hideHeader`) 0f else 6f

        val ox = width / 2 - gridW / 2
        val oy = height / 2 - (gridH + headerH + padding) / 2

        val x = ((mx - ox - pad) / sp).toInt() + int1
        val y = ((my - (oy + headerH + padding) - pad) / sp).toInt() + 1
        if (x !in int1 until int1 + int0 || y < 1) return

        val slot = x + y * 9
        if (slot >= slots) return

        val c = forSlot(slot) ?: return
        if (c.button != mouseButton && !(terminalType == TerminalType.RUBIX && TerminalSolver.`rubix$left`)) return
        c.click()
    }

    fun update(items: List<ItemStack>) {
        compute(items)
    }

    open fun click(slot: Int, button: Int) {
        if (TerminalSimulator.s.value) {
            //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
            val screen = client.screen as? ITerminalSim ?: return
            val slot0 = screen.menu.slots.getOrNull(slot) ?: return

            screen.slotClicked(slot0, slot, button, if (button == 0) ContainerInput.CLONE else ContainerInput.PICKUP)
            TerminalSolver.last = System.currentTimeMillis()
            clicked = true

            return
        }

        guiClick(TerminalAPI.id, slot, if (button == 0) 2 else button, if (button == 0) ContainerInput.CLONE else ContainerInput.PICKUP)
        TerminalSolver.last = System.currentTimeMillis()
        clicked = true
    }

    fun Click.click() {
        click(slot, button)
    }

    protected fun slot(graphics: GuiGraphicsExtractor, x: Float, y: Float, w: Float, h: Float, color: Int, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?, radius: RoundedRectangleRadius = RoundedRectangleRadius.of(TerminalSolver.`ui$slots$roundness` * scale)) {
        if (TerminalSolver.`ui$slots$fill`) RoundedRectangleRenderState.extract(graphics, x, y, w, h, color, radius, pose = pose, scissor = scissor)
        else RoundedRectangleRenderState.extract(graphics, x, y, w, h, color, radius, maxOf(1f, scale), pose = pose, scissor = scissor)
    }

    private fun main(graphics: GuiGraphicsExtractor, x0: Float, y0: Float, gridW: Float, headerH: Float, scale: Float, pose: Matrix3x2f, scissor: ScreenRectangle?) {
        val titleText = terminalType.name.lowercase().replaceFirstChar { it.uppercase() }
        val font = CascadeFonts.arial

        if (TerminalSolver.`ui$hideHeader`) {
            if (!TerminalSolver.`ui$hideTitle`) font.extract(graphics, titleText, (x0 + 1f) * scale, (y0 + (terminalType.slots / 9 - 2) * float - 8f) * scale, TerminalSolver.`ui$titleColor`.rgb, false, 8f * scale)
            return
        }

        val radius = RoundedRectangleRadius.of(TerminalSolver.`ui$roundness` * scale)
        val x1 = x0 * scale
        val y1 = y0 * scale
        val width = gridW * scale
        val height = headerH * scale

        RoundedRectangleRenderState.extract(graphics, x1, y1, width, height, TerminalSolver.`ui$header`.rgb, radius, pose = pose, scissor = scissor)
        RoundedRectangleRenderState.extract(graphics, x1, y1, width, height, TerminalSolver.`ui$border`.rgb, radius, maxOf(1f, scale / 2f), pose = pose, scissor = scissor)

        val size = 11f * scale
        font.extract(graphics, titleText, (x0 + gridW / 2) * scale - font.width(titleText, size) / 2, (y0 + headerH / 2) * scale - (font.regular.height * size) / 2, Mocha.Text.rgba, false, size)
    }
}