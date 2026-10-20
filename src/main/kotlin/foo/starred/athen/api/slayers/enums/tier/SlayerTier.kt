package foo.starred.athen.api.slayers.enums.tier

import com.mojang.serialization.Codec
import foo.starred.kontext.api.format.extension.roman.parseRomanNumeral

enum class SlayerTier(val int: Int, private val _xp: Int) {
    One(1, 5),
    Two(2, 25),
    Three(3, 100),
    Four(4, 500),
    Five(5, 1500);

    val xp: Int // TODO: AATROX
        get() =  _xp

    companion object {
        private val tierRegex = Regex("""(?:^|\s)(?<level>[MDCLXVI]{1,7})(?:\s|$)""")
        val CODEC: Codec<SlayerTier> = Codec.STRING.xmap({ valueOf(it) }, { it.name })

        fun find(string: String): SlayerTier? {
            if ("Atoned Horror" in string) return Five
            if ("Conjoined Brood" in string) return Five

            val (level) = tierRegex.find(string)?.destructured ?: return null
            val i = level.parseRomanNumeral() ?: return null
            return entries.find { it.int == i }
        }
    }
}
