package foo.starred.athen.api.storage

import foo.starred.athen.Athen
import foo.starred.snowbird.handlers.Resourceful
import net.minecraft.resources.Identifier

object ResourceAPI : Resourceful(Athen.modId) {
    fun minecraft(path: String): Identifier {
        return Identifier.withDefaultNamespace(path)
    }
}