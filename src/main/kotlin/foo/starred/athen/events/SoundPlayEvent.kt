package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec3

data class SoundPlayEvent(
    val sound: Identifier,
    val pos: Vec3,
    val volume: Float,
    val pitch: Float
) : AthenEvent(), KBusCancellableTrait
