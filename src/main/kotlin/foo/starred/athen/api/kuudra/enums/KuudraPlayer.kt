package foo.starred.athen.api.kuudra.enums

import foo.starred.athen.Athen
import foo.starred.snowbird.api.level
import foo.starred.snowbird.handlers.delegate.Expirable
import foo.starred.snowbird.utils.stripped
import net.minecraft.world.entity.Entity

class KuudraPlayer(
    val name: String
) {
    var deaths = 0
        internal set

    val entity by Expirable(::d) { !it.isAlive }

    init {
        Athen.LOGGER.debug("Created KuudraPlayer with entity: {}", entity)
    }

    private fun d(): Entity? =
        level?.players()?.find { it.uuid.version() == 4 && it.name.stripped() == name }

    override fun toString(): String =
        "KuudraPlayer(n=$name, d=$deaths, entity: ${entity != null})"
}