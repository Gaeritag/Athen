package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.snowbird.utils.stripped
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.decoration.ArmorStand
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAttributesUpdateEvent

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
            val stripped: String =
                component.stripped()
        }

        data class Named(
            val component: Component,
            val entity: ArmorStand
        ) : AthenEvent() {
            val stripped: String =
                component.stripped()
        }

        data class Health(
            val entity: LivingEntity,
            val old: Float?,
            val new: Float
        ) : AthenEvent()

        data class Equipment(
            val entity: LivingEntity
        ) : AthenEvent()

        data class Attributes(
            val entity: LivingEntity,
            val changed: Map<Holder<Attribute>, EntityAttributesUpdateEvent.ChangedAttribute>,
        ) : AthenEvent()
    }
}
