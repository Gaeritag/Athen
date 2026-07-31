package foo.starred.athen.handlers

import net.minecraft.resources.Identifier
import foo.starred.athen.Athen
import foo.starred.snowbird.handlers.Resourceful

object Resourceful : Resourceful(Athen.modId) {
    fun minecraft(path: String): Identifier {
        return Identifier.withDefaultNamespace(path)
    }
}