package foo.starred.athen.annotations

import foo.starred.athen.api.dungeon.DungeonAPI
import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.api.location.area.impl.PresetSkyBlockArea
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.snowbird.api.ALWAYS_TRUE
import foo.starred.snowbird.api.data.Observable
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnlyIn(
    val areas: Array<PresetSkyBlockArea> = [],
    val islands: Array<PresetSkyBlockIsland> = [],
    val floors: Array<DungeonFloor> = [],
    val skyblock: Boolean = false
) {
    companion object {
        fun get(klass: KClass<*>): Observable<Boolean> {
            val self = klass.java.getAnnotation(OnlyIn::class.java) ?: return ALWAYS_TRUE

            return when {
                self.floors.isNotEmpty() -> DungeonAPI.floor.map { it in self.floors }
                self.areas.isNotEmpty() -> LocationAPI.area.map { it in self.areas }
                self.islands.isNotEmpty() -> LocationAPI.island.map { it in self.islands }
                self.skyblock -> LocationAPI.skyblock
                else -> ALWAYS_TRUE
            }
        }
    }
}
