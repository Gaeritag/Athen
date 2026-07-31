@file:Suppress("Unused", "ObjectPropertyName", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.dungeon.terminals.solver

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW
import foo.starred.athen.annotations.Load
import foo.starred.athen.api.dungeon.terminals.TerminalAPI
import foo.starred.athen.api.dungeon.terminals.TerminalType
import foo.starred.athen.config.Category
import foo.starred.athen.events.DungeonEvent
import foo.starred.athen.events.GuiEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.mixin.accessors.KeyMappingAccessor
import foo.starred.athen.modules.Module
import foo.starred.athen.modules.impl.dungeon.terminals.solver.impl.*
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.nvg.NVGSpecialRenderer
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.ctrl
import foo.starred.snowbird.utils.mouseRX
import foo.starred.snowbird.utils.mouseRY
import java.awt.Color

@Load
object TerminalSolver : Module(
    "Terminal solver",
    "Shows solutions for F7/M7 terminals in a nice custom gui!",
    Category.DUNGEONS
) {
    private val settingsExpandable by config.expandable("Settings")
    val fcDelay by config.slider("First click delay", 350, 150, 1000, "ms").childOf { settingsExpandable }
    val resync by config.slider("Resync timeout", 800, 0, 2000, "ms").childOf { settingsExpandable }
    val dropKey by config.switch("Allow using drop key", true).childOf { settingsExpandable }
    val keybindL by config.keybind("Keybind left click").childOf { settingsExpandable }
    val keybindR by config.keybind("Keybind right click").childOf { settingsExpandable }
    val solve by config.multiCheckbox("Enabled solvers", listOf("Colors", "Melody", "Name", "Numbers", "Panes", "Rubix"), listOf(0, 1, 2, 3, 4, 5)).childOf { settingsExpandable }

    private val rubixExpandable by config.expandable("Rubix")
    val `rubix$left` by config.switch("Left click only").childOf { rubixExpandable }

    private val melodyExpandable by config.expandable("Melody")
    val `melody$num` by config.switch("Number keys").childOf { melodyExpandable }
    val `melody$key0` by config.keybind("Keybind 1", GLFW.GLFW_KEY_1).dependsOn { `melody$num` }.childOf { melodyExpandable }
    val `melody$key1` by config.keybind("Keybind 2", GLFW.GLFW_KEY_2).dependsOn { `melody$num` }.childOf { melodyExpandable }
    val `melody$key2` by config.keybind("Keybind 3", GLFW.GLFW_KEY_3).dependsOn { `melody$num` }.childOf { melodyExpandable }
    val `melody$key3` by config.keybind("Keybind 4", GLFW.GLFW_KEY_4).dependsOn { `melody$num` }.childOf { melodyExpandable }

    private val guiExpandable by config.expandable("GUI")
    val `ui$scale` by config.slider("Scale", 1f, 0.1f, 4f, showDouble = true).childOf { guiExpandable }
    val `ui$roundness` by config.slider("Roundness", 0f, 0f, 10f, showDouble = true).childOf { guiExpandable }
    val `ui$padding` by config.slider("Padding", 5f, 0f, 20f, showDouble = true).childOf { guiExpandable }
    val `ui$gap` by config.slider("Slot gap", 2f, 0f, 10f, showDouble = true).childOf { guiExpandable }
    val `ui$melodyGap` by config.slider("Melody gap", 2f, 0f, 10f, showDouble = true).childOf { guiExpandable }
    val `ui$bg` by config.colorPicker("Background color", Color(0, 0, 0, 150)).childOf { guiExpandable }
    val `ui$border` by config.colorPicker("Border color", Color(Mocha.Mauve.argb, true)).childOf { guiExpandable }

    private val slotsExpandable by config.expandable("Slots")
    val `ui$slots$fill` by config.switch("Fill").childOf { slotsExpandable }
    val `ui$slots$roundness` by config.slider("Roundness", 0f, 0f, 10f, showDouble = true).childOf { slotsExpandable }
    val `ui$numbers$showText` by config.switch("Numbers: Show text", true).childOf { slotsExpandable }

    private val headerExpandable by config.expandable("Header")
    val `ui$hideHeader` by config.switch("Hide header", true).childOf { headerExpandable }
    val `ui$hideTitle` by config.switch("Hide title", true).dependsOn { `ui$hideHeader` }.childOf { headerExpandable }
    val `ui$titleColor` by config.colorPicker("Title color", Color(Mocha.Subtext0.argb, true)).dependsOn { `ui$hideHeader` && !`ui$hideTitle` }.childOf { headerExpandable }
    val `ui$header` by config.colorPicker("Header color", Color(20, 20, 20, 200)).childOf { headerExpandable }

    private val soundExpandable by config.expandable("Sounds")
    val `sound$enabled` by config.switch("Enable sounds").childOf { soundExpandable }
    val clickSound by config.sound("Click sound", "block.note_block.pling").childOf { soundExpandable }

    private val colorExpandable by config.expandable("Solver colors")
    val `colors$correct` by config.colorPicker("Colors: Solution", Color(0, 255, 0, 180)).childOf { colorExpandable }
    val `names$correct` by config.colorPicker("Names: Solution", Color(0, 255, 0, 180)).childOf { colorExpandable }
    val `panes$correct` by config.colorPicker("Panes: Solution", Color(0, 255, 0, 180)).childOf { colorExpandable }
    val `numbers$first` by config.colorPicker("Numbers: 1st", Color(0, 255, 0, 180)).childOf { colorExpandable }
    val `numbers$second` by config.colorPicker("Numbers: 2nd", Color(0, 200, 0, 180)).childOf { colorExpandable }
    val `numbers$third` by config.colorPicker("Numbers: 3rd", Color(0, 150, 0, 180)).childOf { colorExpandable }
    val `rubix$positive` by config.colorPicker("Rubix: Positive", Color(0, 114, 255, 180)).childOf { colorExpandable }
    val `rubix$negative` by config.colorPicker("Rubix: Negative", Color(205, 0, 0, 180)).childOf { colorExpandable }
    val `melody$fill` by config.colorPicker("Melody: Fill", Color(Mocha.Mauve.argb, true)).childOf { colorExpandable }
    val `melody$correct` by config.colorPicker("Melody: Correct", Color(0, 255, 0, 180)).childOf { colorExpandable }
    val `melody$wrong` by config.colorPicker("Melody: Wrong", Color(205, 0, 0, 180)).childOf { colorExpandable }
    val `melody$other` by config.colorPicker("Melody: Other", Color(Mocha.Base.argb, true)).childOf { colorExpandable }

    var last: Long = 0

    init {
        on<PacketEvent.Receive, ClientboundSoundPacket> {
            if (!`sound$enabled`) return@on
            if (sound.value() != SoundEvents.EXPERIENCE_ORB_PICKUP) return@on

            it.cancel()
            clickSound.play()
        }.runWhen(TerminalAPI.opened)

        on<GuiEvent.Render.Screen.Pre> {
            val term = TerminalAPI.terminal?.impl ?: return@on

            cancel()
            NVGSpecialRenderer.draw(graphics, 0, 0, graphics.guiWidth(), graphics.guiHeight()) { term.main() }
        }.runWhen(TerminalAPI.opened)

        on<GuiEvent.Input.Mouse.Press> {
            val term = TerminalAPI.terminal ?: return@on
            if (client.player?.containerMenu?.containerId != TerminalAPI.id) return@on

            cancel()
            if (System.currentTimeMillis() - TerminalAPI.open >= fcDelay) c(mouse = keyEvent.button())
        }.runWhen(TerminalAPI.opened)

        on<GuiEvent.Input.Key.Press> {
            val t = TerminalAPI.terminal ?: return@on
            if (client.player?.containerMenu?.containerId != TerminalAPI.id) return@on
            if (System.currentTimeMillis() - TerminalAPI.open < fcDelay) return@on

            when (keyEvent.key) {
                `melody$key0` if t == TerminalType.MELODY -> {
                    MelodySolver.click(1)
                }

                `melody$key1` if t == TerminalType.MELODY -> {
                    MelodySolver.click(2)
                }

                `melody$key2` if t == TerminalType.MELODY -> {
                    MelodySolver.click(3)
                }

                `melody$key3` if t == TerminalType.MELODY -> {
                    MelodySolver.click(4)
                }

                keybindL -> {
                    c(mouse = 0)
                    cancel()
                }

                keybindR -> {
                    c(mouse = 1)
                    cancel()
                }

                (client.options.keyDrop as? KeyMappingAccessor)?.boundKey?.value if (dropKey) -> {
                    c(mouse = if (!ctrl) 0 else 1)
                    cancel()
                }
            }
        }.runWhen(TerminalAPI.opened)

        on<TickEvent.Client.End> {
            val a = TerminalAPI.terminal ?: return@on
            val b = a.impl

            if (resync == 0) return@on
            if (!b.clicked) return@on
            if (System.currentTimeMillis() - last <= resync) return@on

            b.clicked = false
            b.update((client.screen as? AbstractContainerScreen<*>)?.menu?.items?.subList(0, a.slots) ?: return@on)
            b.onResync()
        }.runWhen(TerminalAPI.opened)

        on<DungeonEvent.Terminal.Update> {
            TerminalAPI.terminal?.impl?.update(items)
        }

        on<DungeonEvent.Terminal.Open> {
            for (a in TerminalType.entries) a.impl.onOpen()
        }

        on<DungeonEvent.Terminal.Close> {
            for (a in TerminalType.entries) a.impl.onClose()
        }
    }

    private fun c(mouse: Int) {
        val solver = TerminalAPI.terminal?.impl ?: return
        val uiScale = 3f * `ui$scale`
        val mx = mouseRX / uiScale
        val my = mouseRY / uiScale

        val width = client.window.width / uiScale
        val height = client.window.height / uiScale

        solver.click(mx, my, width, height, mouse)
    }
}