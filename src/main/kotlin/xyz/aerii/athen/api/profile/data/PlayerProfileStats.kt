package xyz.aerii.athen.api.profile.data

import xyz.aerii.athen.api.profile.data.dungeons.PlayerProfileDungeonStats
import xyz.aerii.athen.api.profile.data.dungeons.PlayerProfileInventoryStats

data class PlayerProfileStats(
    var loading: Boolean = true,
    val dungeons: PlayerProfileDungeonStats? = null,
    val inventory: PlayerProfileInventoryStats? = null
)