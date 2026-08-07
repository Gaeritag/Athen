@file:Suppress("FunctionName", "Unused")

package foo.starred.athen.ducks.entity

import foo.starred.athen.modules.impl.slayer.carry.data.SlayerCarryPlayer
import foo.starred.snowbird.utils.stripped
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import java.lang.ref.WeakReference

interface EntityDuck {
    fun `athen$attachments`(): MutableList<WeakReference<Entity>>
    fun `athen$attach`(): Entity?
    fun `athen$attach`(entity: Entity)

    fun `athen$feat$carry$boss`(): SlayerCarryPlayer?
    fun `athen$feat$carry$boss`(carry: SlayerCarryPlayer?)

    companion object {
        val Entity.parent: Entity?
            get() = (this as? EntityDuck)?.`athen$attach`()

        val Entity.attached: List<Entity>
            get() = (this as? EntityDuck)?.`athen$attachments`()?.mapNotNull { it.get() } ?: emptyList()

        val Entity.attachedNames: List<Component>
            get() = attached.mapNotNull { it.customName }

        val Entity.attachedStripped: List<String>
            get() = attached.mapNotNull { it.customName?.stripped() }

        var Entity.carry: SlayerCarryPlayer?
            get() = (this as? EntityDuck)?.`athen$feat$carry$boss`()
            set(value) {
                (this as? EntityDuck)?.`athen$feat$carry$boss`(value)
            }
    }
}