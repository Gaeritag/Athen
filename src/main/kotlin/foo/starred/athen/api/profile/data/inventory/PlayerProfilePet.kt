package foo.starred.athen.api.profile.data.inventory

import foo.starred.kontext.api.item.data.rarity.SkyBlockItemRarity

data class PlayerProfilePet(
    val name: String,
    private val _rarity: String
) {
    val rarity: SkyBlockItemRarity by lazy {
        SkyBlockItemRarity.get(_rarity) ?: SkyBlockItemRarity.COMMON
    }
}
