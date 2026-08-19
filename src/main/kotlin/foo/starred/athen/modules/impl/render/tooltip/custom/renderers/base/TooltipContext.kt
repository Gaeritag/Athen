package foo.starred.athen.modules.impl.render.tooltip.custom.renderers.base

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent

data class TooltipContext(val graphics: GuiGraphicsExtractor, val font: Font, val components: List<ClientTooltipComponent>, val x: Int, val y: Int, val width: Int, val height: Int, val screenHeight: Int)
