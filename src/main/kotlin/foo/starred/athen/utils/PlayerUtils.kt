package foo.starred.athen.utils

import net.minecraft.world.inventory.ClickType
import foo.starred.snowbird.api.client

fun guiClick(id: Int, index: Int, button: Int = 0, clickType: ClickType = ClickType.PICKUP) {
    val player = client.player ?: return
    //~ if >= 26.1 'handleInventoryMouseClick' -> 'handleContainerInput'
    client.gameMode?.handleInventoryMouseClick(id, index, button, clickType, player)
}