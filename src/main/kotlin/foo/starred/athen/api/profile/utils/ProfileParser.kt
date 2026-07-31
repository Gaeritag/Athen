package foo.starred.athen.api.profile.utils

import com.google.gson.JsonObject
import foo.starred.athen.api.profile.data.PlayerProfileStats
import foo.starred.athen.api.profile.data.dungeons.PlayerProfileDungeonStats
import foo.starred.athen.api.profile.data.dungeons.PlayerProfileInventoryStats

object ProfileParser {
    fun get(json: JsonObject): PlayerProfileStats {
        val dungeons = json.getAsJsonObject("dungeons")

        return PlayerProfileStats(
            loading = false,
            dungeons = PlayerProfileDungeonStats(
                catacombs = dungeons?.get("catacombs_level")?.asInt,
                secrets = dungeons?.get("secrets")?.asInt,
                `secrets$average` = run {
                    val s = dungeons?.get("secrets")?.asInt
                    val r = dungeons?.get("total_runs")?.asInt

                    if (s != null && r != null && r > 0) s.toDouble() / r else null
                },
                `pbs$normal` = dungeons?.getAsJsonObject("catacombs")?.getAsJsonObject("fastest_time_s_plus")?.pb(),
                `pbs$master` = dungeons?.getAsJsonObject("master_catacombs")?.getAsJsonObject("fastest_time_s_plus")?.pb()
            ),
            inventory = PlayerProfileInventoryStats(
                ProfileNBT.armor(json.get("armorData")?.asString),
                ProfileNBT.mp(json.get("talismanBagData")?.asString, json.get("consumedRiftPrism")?.asBoolean ?: false) ?: 0
            )
        )
    }

    private fun JsonObject.pb(): Map<Int, Long> {
        return buildMap {
            for ((k, v) in entrySet()) k.toIntOrNull()?.let { put(it, v.asLong) }
        }
    }
}