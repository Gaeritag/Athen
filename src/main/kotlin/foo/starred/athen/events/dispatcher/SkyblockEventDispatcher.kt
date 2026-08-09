@file:Suppress("Unused")

package foo.starred.athen.events.dispatcher

import foo.starred.athen.annotations.Priority
import foo.starred.athen.events.*
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAttributesUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityEquipmentUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.minecraft.sounds.SoundPlayedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemTooltipEvent

@Priority
object SkyblockEventDispatcher {
    init {
        SkyBlockAPI.eventBus.register(SkyblockEventDispatcher)
    }

    @Subscription
    fun onTabListChange(event: TabListChangeEvent) {
        TabListEvent.Change(event.old, event.new).post()
    }

    @Subscription
    fun onScoreboardTitleUpdate(event: ScoreboardTitleUpdateEvent) {
        ScoreboardEvent.UpdateTitle(event.old, event.new).post()
    }

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        ScoreboardEvent.Update(event.old, event.new, event.oldComponents, event.newComponents).post()
    }

    @Subscription(receiveCancelled = true)
    fun onEntityEquipment(event: EntityEquipmentUpdateEvent) {
        EntityEvent.Update.Equipment(event.entity).post()
    }

    @Subscription(receiveCancelled = true)
    fun onEntityAttribute(event: EntityAttributesUpdateEvent) {
        EntityEvent.Update.Attributes(event.entity, event.changed).post()
    }

    @Subscription
    fun onTooltipRender(event: ItemTooltipEvent) {
        GuiEvent.Tooltip.Render(event.item, event.tooltip).post()
    }

    @Subscription
    fun onSoundPlay(event: SoundPlayedEvent) {
        if (SoundPlayEvent(event.sound, event.pos, event.volume, event.pitch).post()) event.cancel()
    }
}