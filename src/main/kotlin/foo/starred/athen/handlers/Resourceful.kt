package foo.starred.athen.handlers

import foo.starred.athen.Athen
import foo.starred.snowbird.handlers.Resourceful
import net.minecraft.resources.Identifier

object Resourceful : Resourceful(Athen.modId) {
    fun minecraft(path: String): Identifier {
        return Identifier.withDefaultNamespace(path)
    }
}