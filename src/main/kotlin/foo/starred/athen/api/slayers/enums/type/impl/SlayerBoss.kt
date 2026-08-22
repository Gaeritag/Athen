package foo.starred.athen.api.slayers.enums.type.impl

import com.mojang.serialization.Codec
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.base.ISlayerType

enum class SlayerBoss(override val display: String, val short: String, val max: SlayerTier = SlayerTier.Four, override val names: Set<String> = setOf(display)) : ISlayerType {
    Revenant("Revenant Horror", "Rev", SlayerTier.Five, setOf("Revenant Horror", "Atoned Horror")),
    Tarantula("Tarantula Broodfather", "Tara", SlayerTier.Five, setOf("Tarantula Broodfather", "Conjoined Brood")),
    Sven("Sven Packmaster", "Sven"),
    Voidgloom("Voidgloom Seraph", "Void"),
    Inferno("Inferno Demonlord", "Blaze"),
    Vampire("Bloodfiend", "Vamp", SlayerTier.Five);

    companion object {
        val NAMES: Set<String> = entries.map { it.display }.toSet()
        val SHORTS: Set<String> = entries.map { it.short }.toSet()
        val SHORTS0: Set<String> = entries.map { it.short.lowercase() }.toSet()

        val CODEC: Codec<SlayerBoss> = Codec.STRING.xmap({ valueOf(it) }, { it.name })
    }
}