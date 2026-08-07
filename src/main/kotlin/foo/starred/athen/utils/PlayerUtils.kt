package foo.starred.athen.utils

import foo.starred.snowbird.api.client
import net.minecraft.world.inventory.ClickType

fun guiClick(id: Int, index: Int, button: Int = 0, clickType: ClickType = ClickType.PICKUP) {
    val player = client.player ?: return
    //~ if >= 26.1 'handleInventoryMouseClick' -> 'handleContainerInput'
    client.gameMode?.handleInventoryMouseClick(id, index, button, clickType, player)
}