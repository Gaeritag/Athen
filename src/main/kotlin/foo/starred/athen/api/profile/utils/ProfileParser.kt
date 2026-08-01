package foo.starred.athen.api.profile.utils

import com.google.gson.JsonObject
import foo.starred.athen.api.dungeon.enums.DungeonClass
import foo.starred.athen.api.profile.data.PlayerProfileStats
import foo.starred.athen.api.profile.data.dungeons.PlayerProfileDungeonStats
import foo.starred.athen.api.profile.data.dungeons.PlayerProfileInventoryStats
import foo.starred.athen.api.profile.data.inventory.PlayerProfilePet

object ProfileParser {
    fun get(name: String, json: JsonObject): PlayerProfileStats {
        val dungeons = json.getAsJsonObject("dungeons")
        val inventory = json.getAsJsonObject("inventory")
        val talismans = inventory?.getAsJsonObject("bags")?.getAsJsonObject("talismans")
        val pets = inventory?.getAsJsonObject("pets")

        return PlayerProfileStats(
            name,
            PlayerProfileDungeonStats(
                catacombs = dungeons?.get("catacombs_level")?.asInt,
                secrets = dungeons?.get("secrets")?.asInt,
                total = dungeons?.get("total_runs")?.asInt,
                blood = dungeons?.get("blood_mobs_killed")?.asInt,
                classes = dungeons?.getAsJsonObject("player_classes")?.classes(),
                `secrets$average` = dungeons?.average(),
                `pbs$normal` = dungeons?.getAsJsonObject("catacombs")?.getAsJsonObject("fastest_time_s_plus")?.pb(),
                `pbs$master` = dungeons?.getAsJsonObject("master_catacombs")?.getAsJsonObject("fastest_time_s_plus")?.pb()
            ),
            PlayerProfileInventoryStats(
                pet = pets?.getAsJsonObject("active")?.pet(),
                pets = pets?.getAsJsonArray("all")?.mapNotNull { it.asJsonObject.pet() },
                armor = ProfileNBT.armor(inventory?.getAsJsonObject("armors")?.get("active")?.asString),
                mp = ProfileNBT.mp(talismans?.get("bag")?.asString, talismans?.get("consumed_rift_prism")?.asBoolean ?: false) ?: 0
            )
        )
    }

    private fun JsonObject.pb(): Map<Int, Long> {
        return buildMap {
            for ((k, v) in entrySet()) k.toIntOrNull()?.let { put(it, v.asLong) }
        }
    }

    private fun JsonObject.average(): Double? {
        val s = get("secrets")?.asInt
        val r = get("total_runs")?.asInt

        return if (s != null && r != null && r > 0) s.toDouble() / r else null
    }

    private fun JsonObject.classes(): Map<DungeonClass, Int> {
        return buildMap {
            for ((k, v) in entrySet()) DungeonClass.get(k)?.let { put(it, v.asInt) }
        }
    }

    private fun JsonObject.pet(): PlayerProfilePet? {
        val type = get("type")?.asString ?: return null
        val tier = get("tier")?.asString ?: return null
        val name = type.lowercase().split("_").joinToString(" ") { a -> a.replaceFirstChar { b -> b.titlecase() } }

        return PlayerProfilePet(name, tier)
    }
}