package foo.starred.athen.events

import foo.starred.athen.events.core.CancellableEvent
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.phys.Vec3

data class SoundPlayEvent(
    val sound: SoundEvent,
    val pos: Vec3,
    val volume: Float,
    val pitch: Float
) : CancellableEvent()