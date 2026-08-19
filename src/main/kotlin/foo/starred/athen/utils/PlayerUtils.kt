package foo.starred.athen.utils

import foo.starred.snowbird.api.client
import net.minecraft.world.inventory.ContainerInput

fun guiClick(id: Int, index: Int, button: Int = 0, clickType: ContainerInput = ContainerInput.PICKUP) {
    val player = client.player ?: return
    client.gameMode?.handleContainerInput(id, index, button, clickType, player)
}