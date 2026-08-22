package foo.starred.athen.accessors

import foo.starred.athen.mixin.accessors.AbstractContainerScreenAccessor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

val AbstractContainerScreen<*>.hovered: Slot?
    get() = (this as AbstractContainerScreenAccessor).hovered()

val AbstractContainerScreen<*>.left: Int
    get() = (this as AbstractContainerScreenAccessor).leftPos()

val AbstractContainerScreen<*>.top: Int
    get() = (this as AbstractContainerScreenAccessor).topPos()