@file:Suppress("PropertyName")

package foo.starred.athen.api.profile.data.dungeons

import foo.starred.athen.api.dungeon.enums.DungeonClass

data class PlayerProfileDungeonStats(
    val catacombs: Int? = null,
    val secrets: Int? = null,
    val total: Int? = null,
    val blood: Int? = null,
    val classes: Map<DungeonClass, Int>? = null,
    val `secrets$average`: Double? = null,
    val `pbs$normal`: Map<Int, Long>? = null,
    val `pbs$master`: Map<Int, Long>? = null
)