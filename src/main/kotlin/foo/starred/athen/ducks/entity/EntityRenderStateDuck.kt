@file:Suppress("FunctionName")

package foo.starred.athen.ducks.entity

import net.minecraft.world.entity.Entity

interface EntityRenderStateDuck {
    fun `athen$getEntity`(): Entity?
    fun `athen$setEntity`(entity: Entity?)
}