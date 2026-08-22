package foo.starred.athen.annotations

import foo.starred.athen.api.location.SkyBlockArea
import foo.starred.athen.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor

/**
 * Marks a [foo.starred.athen.modules.Module] to only be enabled in a certain region.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnlyIn(
    val areas: Array<SkyBlockArea> = [],
    val islands: Array<SkyBlockIsland> = [],
    val floors: Array<DungeonFloor> = [],
    val skyblock: Boolean = false
)