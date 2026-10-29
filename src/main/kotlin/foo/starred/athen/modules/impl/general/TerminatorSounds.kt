@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.SoundPlayEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.GenericKontextItemDataResolver
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.BowItem

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object TerminatorSounds : Module(
    "Terminator sounds",
    "Custom sounds for terminator!",
    ConfigCategory.GENERAL
) {
    private val wall by config.switch("Wall hit sound", true)
    private val sound0 by config.sound("Sound")

    init {
        on<SoundPlayEvent> {
            if (sound == SoundEvents.ARROW_SHOOT.location()) return@on cancel()
            if (sound == SoundEvents.ARROW_HIT.location() && !wall) return@on cancel()
            if (sound != SoundEvents.ARROW_HIT.location() && sound != SoundEvents.ARROW_HIT_PLAYER.location()) return@on

            val i = client.player?.mainHandItem ?: return@on
            if (i.item !is BowItem) return@on
            if (i.get<GenericKontextItemDataResolver.ID, String>() != "TERMINATOR") return@on

            cancel()
            sound0.play()
        }
    }
}
