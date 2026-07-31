package foo.starred.athen.api.slayers.enums.type.base

import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.api.slayers.enums.type.impl.SlayerDemon
import foo.starred.athen.api.slayers.enums.type.impl.SlayerMini

interface ISlayerType {
    val display: String
    val names: Set<String>

    companion object {
        val ALL: List<ISlayerType> = listOf(SlayerBoss.entries, SlayerDemon.entries, SlayerMini.entries).flatten()

        object Names {
            val all: Set<String> = ALL.flatMap { it.names }.toSet()
            val map: Map<String, ISlayerType> = ALL.flatMap { a -> a.names.map { b -> b to a } }.toMap()
        }
    }
}