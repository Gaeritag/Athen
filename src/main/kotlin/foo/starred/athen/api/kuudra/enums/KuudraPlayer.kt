package foo.starred.athen.api.kuudra.enums

import foo.starred.athen.Athen
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.lazy.RefreshableLazy
import net.minecraft.world.entity.Entity

class KuudraPlayer(
    val name: String
) {
    var deaths = 0
        internal set

    val entity by RefreshableLazy(::d) { !it.isAlive }

    init {
        Athen.LOGGER.debug("Created KuudraPlayer with entity: {}", entity)
    }

    private fun d(): Entity? {
        return client.level?.players()?.find { it.uuid.version() == 4 && it.gameProfile.name() == name }
    }

    override fun toString(): String {
        return "KuudraPlayer(n=$name, d=$deaths, entity: ${entity != null})"
    }
}
