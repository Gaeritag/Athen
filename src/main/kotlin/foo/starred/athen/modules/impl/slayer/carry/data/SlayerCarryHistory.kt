package foo.starred.athen.modules.impl.slayer.carry.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import java.util.*

data class SlayerCarryHistory(
    val name: String,
    val type: SlayerBoss,
    val tier: SlayerTier?,
    val amount: Int,
    val duration: Long,
    val completed: Long = System.currentTimeMillis()
) {
    companion object {
        val CODEC: Codec<SlayerCarryHistory> = RecordCodecBuilder.create { a ->
            a.group(
                Codec.STRING.fieldOf("name").forGetter(SlayerCarryHistory::name),
                SlayerBoss.CODEC.fieldOf("type").forGetter(SlayerCarryHistory::type),
                SlayerTier.CODEC.optionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                Codec.INT.fieldOf("amount").forGetter(SlayerCarryHistory::amount),
                Codec.LONG.fieldOf("duration").forGetter(SlayerCarryHistory::duration),
                Codec.LONG.fieldOf("completed").forGetter(SlayerCarryHistory::completed)
            ).apply(a) { name, type, tier, amount, duration, completed ->
                SlayerCarryHistory(
                    name,
                    type,
                    tier.orElse(null),
                    amount,
                    duration,
                    completed
                )
            }
        }
    }
}