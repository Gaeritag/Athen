@file:Suppress("PropertyName")

package xyz.aerii.athen.api.profile.data.dungeons

data class PlayerProfileDungeonStats(
    var catacombs: Int? = null,
    var secrets: Int? = null,
    var `secrets$average`: Double? = null,
    var `pbs$normal`: Map<Int, Long>? = null,
    var `pbs$master`: Map<Int, Long>? = null
)