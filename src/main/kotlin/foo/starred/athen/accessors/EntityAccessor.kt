@file:Suppress("FunctionName", "Unused", "Deprecation")

package foo.starred.athen.accessors

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import foo.starred.athen.ducks.entity.EntityDuck
import foo.starred.snowbird.utils.stripped

@Deprecated("Use EntityDuck")
val Entity.parent: Entity?
    get() = (this as? EntityDuck)?.`athen$attach`()

@Deprecated("Use EntityDuck")
val Entity.attached: List<Entity>
    get() = (this as? EntityDuck)?.`athen$attachments`()?.mapNotNull { it.get() } ?: emptyList()

@Deprecated("Use EntityDuck")
val Entity.attachedNames: List<Component>
    get() = attached.mapNotNull { it.customName }

@Deprecated("Use EntityDuck")
val Entity.attachedStripped: List<String>
    get() = attached.mapNotNull { it.customName?.stripped() }