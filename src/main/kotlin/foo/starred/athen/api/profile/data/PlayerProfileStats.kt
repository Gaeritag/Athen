package foo.starred.athen.api.profile.data

import foo.starred.athen.api.profile.data.dungeons.PlayerProfileDungeonStats
import foo.starred.athen.api.profile.data.dungeons.PlayerProfileInventoryStats

data class PlayerProfileStats(
    val name: String,
    val dungeons: PlayerProfileDungeonStats? = null,
    val inventory: PlayerProfileInventoryStats? = null
)