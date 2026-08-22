package foo.starred.athen.api.profile.utils

import foo.starred.athen.api.profile.data.PlayerProfileStack
import foo.starred.snowbird.utils.stripped
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import kotlin.io.encoding.Base64
import kotlin.jvm.optionals.getOrNull

object ProfileNBT {
    fun armor(string: String?): List<PlayerProfileStack>? {
        if (string.isNullOrEmpty()) return null

        return runCatching {
            val compound0 = NbtIo.readCompressed(Base64.decode(string).inputStream(), NbtAccounter.unlimitedHeap())
            val list0 = compound0.getList("i").getOrNull() ?: return null

            list0.indices.mapNotNull { i ->
                val compound = list0.getCompound(i).getOrNull()?.takeIf { it.size() > 0 } ?: return@mapNotNull null

                val tag = compound.get("tag")?.asCompound()?.get()
                val display = tag?.get("display")?.asCompound()?.get()
                val id = tag?.get("ExtraAttributes")?.asCompound()?.get()?.get("id")?.asString()?.get()
                val name = display?.get("Name")?.asString()?.get()
                val lore = display?.get("Lore")?.asList()?.get()?.mapNotNull { it.asString().getOrNull() }
                PlayerProfileStack(i, id, name, lore)
            }
        }.getOrNull()
    }

    fun mp(string: String?, consumedPrism: Boolean): Int? {
        if (string.isNullOrEmpty()) return null

        return runCatching {
            val items = NbtIo.readCompressed(Base64.decode(string).inputStream(), NbtAccounter.unlimitedHeap()).getList("i").getOrNull() ?: return null
            val map = mutableMapOf<String, Int>()

            for (i in items.indices) {
                val tag = items.getCompound(i).getOrNull()?.takeIf { it.size() > 0 }?.get("tag")?.asCompound()?.get() ?: continue

                val extra = tag.get("ExtraAttributes")?.asCompound()?.get() ?: continue
                val id = extra.get("id")?.asString()?.get() ?: continue

                val lore = tag.get("display")?.asCompound()?.get()?.get("Lore")?.asList()?.get()?.mapNotNull { it.asString().getOrNull() }.orEmpty()
                if (lore.any { "§7§4☠ §cRequires" in it }) continue

                val power = lore.firstNotNullOfOrNull { line ->
                    line.stripped().takeIf { it.startsWith("Accessory Power: +") }?.substringAfter('+')?.trim()?.toIntOrNull()
                } ?: 0

                val id0 = when {
                    id.startsWith("PARTY_HAT_") || id.startsWith("BALLOON_HAT_") -> "PARTY_HAT"
                    else -> id
                }

                map[id0] = maxOf(map[id0] ?: 0, power)
            }

            map.values.sum() + if (consumedPrism) 11 else 0
        }.getOrNull()
    }
}