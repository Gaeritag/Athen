@file:Suppress("Unused")

package foo.starred.athen.api.slayers.enums.drop.base

import foo.starred.athen.api.slayers.enums.drop.data.SlayerDropChance
import foo.starred.athen.api.slayers.enums.drop.data.SlayerDropGrade
import foo.starred.athen.api.slayers.enums.drop.data.SlayerDropParserType
import foo.starred.athen.api.slayers.enums.drop.impl.*
import net.minecraft.world.item.Item

interface ISlayerDrop {
    val display: String
    val grade: SlayerDropGrade
    val drop: SlayerDropChance
    val parser: SlayerDropParserType
    val item: Item
    val str2: String?
    val display2: String

    companion object {
        @JvmStatic
        val ALL: Set<ISlayerDrop> = buildSet {
            addAll(InfernoDrops.entries)
            addAll(RevenantDrops.entries)
            addAll(SvenDrops.entries)
            addAll(TarantulaDrops.entries)
            addAll(VampireDrops.entries)
            addAll(VoidgloomDrops.entries)
        }

        object Names {
            val ALL: Set<String> =
                buildSet {
                    for (drop in this@Companion.ALL) add(drop.display2)
                }

            val ALL0: Set<String> =
                buildSet {
                    for (drop in this@Companion.ALL) add(drop.display2.lowercase())
                }

            val LOOKUP: Map<String, ISlayerDrop> =
                buildMap {
                    for (drop in this@Companion.ALL) put(drop.display2, drop)
                }

            val LOOKUP0: Map<String, ISlayerDrop> =
                buildMap {
                    for (drop in this@Companion.ALL) put(drop.display2.lowercase(), drop)
                }
        }
    }
}