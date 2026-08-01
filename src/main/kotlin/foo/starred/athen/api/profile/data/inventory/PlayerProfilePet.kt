package foo.starred.athen.api.profile.data.inventory

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity

data class PlayerProfilePet(
    val name: String,
    private val _rarity: String
) {
    val rarity: SkyBlockRarity by lazy {
        SkyBlockRarity.fromName(_rarity)
    }
}