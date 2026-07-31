package foo.starred.athen.modules.impl.slayer

import net.minecraft.sounds.SoundEvents
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.config.Category
import foo.starred.athen.events.SoundPlayEvent
import foo.starred.athen.modules.Module

@Load
@OnlyIn(skyblock = true)
object SlayerSounds : Module(
    "Slayer sounds",
    "Toggles for slayer sounds!",
    Category.SLAYER
) {
    private val disableEnder by config.switch("Disable voidgloom sounds", true)
    private val enderSet = setOf(SoundEvents.ENDERMAN_STARE, SoundEvents.ENDERMAN_SCREAM)

    init {
        on<SoundPlayEvent> {
            if (!disableEnder) return@on
            if (sound in enderSet) cancel()
        }
    }
}