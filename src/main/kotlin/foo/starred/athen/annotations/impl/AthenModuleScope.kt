package foo.starred.athen.annotations.impl

import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.api.location.area.impl.PresetSkyBlockArea
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.kontext.api.ALWAYS_TRUE
import foo.starred.kontext.api.data.Observable
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AthenModuleScope(
    val areas: Array<PresetSkyBlockArea> = [],
    val islands: Array<PresetSkyBlockIsland> = [],
    val skyblock: Boolean = false
) {
    companion object {
        fun get(klass: KClass<*>): Observable<Boolean> {
            val self = klass.java.getAnnotation(AthenModuleScope::class.java) ?: return ALWAYS_TRUE

            return when {
                self.areas.isNotEmpty() -> LocationAPI.area.map { it in self.areas }
                self.islands.isNotEmpty() -> LocationAPI.island.map { it in self.islands }
                self.skyblock -> LocationAPI.skyblock
                else -> ALWAYS_TRUE
            }
        }
    }
}
