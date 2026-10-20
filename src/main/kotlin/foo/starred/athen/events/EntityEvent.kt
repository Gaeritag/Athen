package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

sealed class EntityEvent {
    data class Load(
        val entity: Entity
    ) : AthenEvent()

    data class Unload(
        val entity: Entity
    ) : AthenEvent()

    data class Death(
        val entity: Entity
    ) : AthenEvent()

    sealed class Update {
        data class Attach(
            val component: Component,
            val entity: Entity
        ) : AthenEvent() {
            val stripped: String by lazy(LazyThreadSafetyMode.NONE) {
                component.stripped()
            }
        }

        data class Named(
            val component: Component,
            val entity: ArmorStand
        ) : AthenEvent() {
            val stripped: String by lazy(LazyThreadSafetyMode.NONE) {
                component.stripped()
            }
        }

        data class Health(
            val entity: LivingEntity,
            val old: Float?,
            val new: Float
        ) : AthenEvent()

        data class Equipment(
            val entity: LivingEntity
        ) : AthenEvent()
    }
}
