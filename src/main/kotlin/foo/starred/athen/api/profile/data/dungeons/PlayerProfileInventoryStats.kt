package foo.starred.athen.api.profile.data.dungeons

import foo.starred.athen.api.profile.data.PlayerProfileStack

data class PlayerProfileInventoryStats(
    var armor: List<PlayerProfileStack>? = null,
    var mp: Int = 0
)