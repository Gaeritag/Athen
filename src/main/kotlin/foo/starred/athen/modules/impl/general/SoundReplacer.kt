@file:Suppress("unused")

package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.SoundPlayEvent
import foo.starred.athen.modules.Module

@LoadableAthenModule
object SoundReplacer : Module(
    "Sound replacer",
    "Replaces all sounds, with a sound that you select.",
    ConfigCategory.GENERAL
) {
    private val sound0 by config.sound("Sound", "entity.cat.purreow")

    init {
        on<SoundPlayEvent> {
            val r = sound0.sound.takeIf { it.location() != sound } ?: return@on

            cancel()
            sound0.play()
        }
    }
}
