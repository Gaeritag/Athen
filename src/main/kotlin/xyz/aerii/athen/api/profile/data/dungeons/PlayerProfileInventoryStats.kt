package xyz.aerii.athen.api.profile.data.dungeons

import xyz.aerii.athen.api.profile.data.PlayerProfileStack

data class PlayerProfileInventoryStats(
    var armor: List<PlayerProfileStack>? = null,
    var mp: Int = 0
)