package foo.starred.athen.api.profile.data.dungeons

import foo.starred.athen.api.profile.data.PlayerProfileStack
import foo.starred.athen.api.profile.data.inventory.PlayerProfilePet

data class PlayerProfileInventoryStats(
    val pet: PlayerProfilePet? = null,
    val pets: List<PlayerProfilePet>? = null,
    val armor: List<PlayerProfileStack>? = null,
    val mp: Int = 0
)