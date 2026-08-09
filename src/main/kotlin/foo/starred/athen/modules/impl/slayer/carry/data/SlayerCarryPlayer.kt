package foo.starred.athen.modules.impl.slayer.carry.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.snowbird.api.level
import foo.starred.snowbird.utils.toDuration
import net.minecraft.world.entity.Entity
import java.util.*

data class SlayerCarryPlayer(
    val name: String,
    val type: SlayerBoss,
    val tier: SlayerTier?,
    var max: Int = 0,
    var done: Int = 0,
    var first: Long = 0,
    var last: Long = 0
) {
    val entity = Ticking(5) {
        level?.players()?.find { it.name.string == name }
    }

    var tick: Int = 0
        private set

    var boss: Entity? = null
        private set

    fun spawn(entity: Entity): Boolean {
        if (boss != null) return false
        boss = entity
        tick = Scheduler.ticks.server
        return true
    }

    fun die(entity: Entity): Result? {
        if (boss == null) return null
        val now = System.currentTimeMillis()

        if (first == 0L) first = now
        last = now
        boss = null
        done++

        return Result(
            entity.tickCount / 20.0,
            Scheduler.ticks.server - tick,
            done >= max,
            done,
            max,
            (last - first) / 1000.0,
        )
    }

    fun reset() {
        last = System.currentTimeMillis()
        boss = null
    }

    override fun toString(): String {
        val now = System.currentTimeMillis()
        val since = if (last != 0L) ((now - last) / 1000.0).toDuration() else "N/A"
        val short = "<dark_gray>[<gray>${type.short}${tier?.let { " T${it.int}" } ?: " Any"}<dark_gray>]"
        return "<dark_gray>- <aqua>$name $short<r>: <aqua>$done<r>/<aqua>$max <gray>($since | ${rate()})"
    }

    private fun rate(): String {
        if (done <= 2) return "N/A"
        if (first == 0L) return "N/A"

        val t0 = (System.currentTimeMillis() - first) / 1000
        if (t0 <= 0) return "N/A"

        return "${done * 3600 / t0}/h"
    }

    companion object {
        data class Result(
            val time: Double,
            val ticks: Int,
            val last: Boolean,
            val done: Int,
            val max: Int,
            val time0: Double
        )

        val CODEC: Codec<SlayerCarryPlayer> = RecordCodecBuilder.create { a ->
            a.group(
                Codec.STRING.fieldOf("name").forGetter(SlayerCarryPlayer::name),
                SlayerBoss.CODEC.fieldOf("type").forGetter(SlayerCarryPlayer::type),
                SlayerTier.CODEC.optionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                Codec.INT.fieldOf("max").forGetter(SlayerCarryPlayer::max),
                Codec.INT.fieldOf("done").forGetter(SlayerCarryPlayer::done),
                Codec.LONG.fieldOf("first").forGetter(SlayerCarryPlayer::first),
                Codec.LONG.fieldOf("last").forGetter(SlayerCarryPlayer::last)
            ).apply(a) { name, type, tier, max, done, first, last ->
                SlayerCarryPlayer(
                    name,
                    type,
                    tier.orElse(null),
                    max,
                    done,
                    first,
                    last
                )
            }
        }
    }
}