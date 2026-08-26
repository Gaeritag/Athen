package foo.starred.athen.api.profile

import com.google.gson.JsonArray
import foo.starred.athen.Athen
import foo.starred.athen.api.network.http.WebAPI.request
import foo.starred.athen.api.profile.data.PlayerProfileStats
import foo.starred.athen.api.profile.utils.ProfileParser
import foo.starred.athen.utils.api

object ProfileAPI {
    fun get(
        username: String,
        inventory: Boolean = false,
        onSuccess: (PlayerProfileStats) -> Unit
    ) {
        get(listOf(username), inventory) { onSuccess(it.getValue(username)) }
    }

    fun get(
        usernames: Collection<String>,
        inventory: Boolean = false,
        onSuccess: (Map<String, PlayerProfileStats>) -> Unit
    ) {
        val names = usernames.joinToString(",")

        "hypixel?names=$names".api.request {
            if (inventory) headers("Include-Inventory" to "true")

            success<JsonArray> { array ->
                onSuccess(buildMap {
                    for (element in array) {
                        val obj = element.asJsonObject
                        val name = obj.get("first").asString
                        val json = obj.getAsJsonObject("second")

                        put(name, if (json.has("error")) PlayerProfileStats(name) else ProfileParser.get(name, json))
                    }
                })
            }

            error {
                Athen.LOGGER.error("Failed to batch fetch profile stats for $usernames", it)
                onSuccess(usernames.associateWith { a -> PlayerProfileStats(a) })
            }
        }
    }
}
