package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

sealed class GuiEvent {
    sealed class Render {
        sealed class Any {
            data class Pre(
                val graphics: GuiGraphicsExtractor
            ) : AthenEvent()

            data class Main(
                val graphics: GuiGraphicsExtractor
            ) : AthenEvent()

            data class Post(
                val graphics: GuiGraphicsExtractor
            ) : AthenEvent()
        }

        sealed class Screen {
            data class Pre(
                val graphics: GuiGraphicsExtractor
            ) : AthenEvent(), KBusCancellableTrait

            data class Post(
                val graphics: GuiGraphicsExtractor
            ) : AthenEvent()
        }
    }

    sealed class Open {
        data class Container(
            val screen: AbstractContainerScreen<*>
        ) : AthenEvent() {
            val stripped = screen.title.stripped()
        }

        data class Any(
            val screen: net.minecraft.client.gui.screens.Screen
        ) : AthenEvent() {
            val stripped = screen.title.stripped()
        }
    }

    sealed class Close {
        data class Container(
            val screen: AbstractContainerScreen<*>
        ) : AthenEvent() {
            val stripped = screen.title.stripped()
        }

        data class Any(
            val screen: net.minecraft.client.gui.screens.Screen
        ) : AthenEvent() {
            val stripped = screen.title.stripped()
        }
    }

    sealed class Slots {
        sealed class Render {
            sealed class Any {
                data class Pre(
                    val graphics: GuiGraphicsExtractor,
                    val slot: Slot
                ) : AthenEvent(), KBusCancellableTrait

                data class Post(
                    val graphics: GuiGraphicsExtractor,
                    val slot: Slot
                ) : AthenEvent()
            }

            sealed class Menu {
                data class Start(
                    val graphics: GuiGraphicsExtractor
                ) : AthenEvent(), KBusCancellableTrait

                data class End(
                    val graphics: GuiGraphicsExtractor
                ) : AthenEvent()
            }

            sealed class Hotbar {
                data class Pre(
                    val graphics: GuiGraphicsExtractor,
                    val item: ItemStack,
                    val x: Int,
                    val y: Int
                ) : AthenEvent(), KBusCancellableTrait

                data class Post(
                    val graphics: GuiGraphicsExtractor,
                    val item: ItemStack,
                    val x: Int,
                    val y: Int
                ) : AthenEvent()
            }
        }

        sealed class Input {
            data class Click(
                val slot: Slot?,
                val slotId: Int,
                val mouseButton: Int,
                val clickType: ContainerInput
            ) : AthenEvent(), KBusCancellableTrait

            data class Hover(
                val slot: Slot
            ) : AthenEvent()

            data class Unhover(
                val slot: Slot
            ) : AthenEvent()
        }
    }

    sealed class Items {
        sealed class Render {
            data class Pre(
                val graphics: GuiGraphicsExtractor,
                val item: ItemStack,
                val x: Int,
                val y: Int
            ) : AthenEvent()

            data class Post(
                val graphics: GuiGraphicsExtractor,
                val item: ItemStack,
                val x: Int,
                val y: Int
            ) : AthenEvent()
        }
    }

    sealed class Tooltip {
        data class Render(
            val item: ItemStack,
            val tooltip: MutableList<Component>
        ) : AthenEvent()

        data class Update(
            val item: ItemStack,
            val tooltip: MutableList<Component>
        ) : AthenEvent()
    }

    sealed class Input {
        sealed class Key {
            data class Press(
                val keyEvent: KeyEvent
            ) : AthenEvent(), KBusCancellableTrait

            data class Release(
                val keyEvent: KeyEvent
            ) : AthenEvent()
        }

        sealed class Mouse {
            data class Press(
                val keyEvent: MouseButtonEvent
            ) : AthenEvent(), KBusCancellableTrait

            data class Release(
                val keyEvent: MouseButtonEvent
            ) : AthenEvent()

            data class Scroll(
                val amount: Double
            ) : AthenEvent(), KBusCancellableTrait
        }
    }
}
