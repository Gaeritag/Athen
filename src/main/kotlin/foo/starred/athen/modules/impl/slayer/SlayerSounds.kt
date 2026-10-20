package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.SoundPlayEvent
import foo.starred.athen.modules.Module
import net.minecraft.sounds.SoundEvents

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object SlayerSounds : Module(
    "Slayer sounds",
    "Toggles for slayer sounds!",
    ConfigCategory.SLAYER
) {
    private val disableEnder by config.switch("Disable voidgloom sounds", true)
    private val enderSet = setOf(SoundEvents.ENDERMAN_STARE.location(), SoundEvents.ENDERMAN_SCREAM.location())

    init {
        on<SoundPlayEvent> {
            if (!disableEnder) return@on
            if (sound in enderSet) cancel()
        }
    }
}
