package foo.starred.athen.api.profile

import com.google.gson.JsonArray
import foo.starred.athen.Athen
import foo.starred.athen.api.profile.data.PlayerProfileStats
import foo.starred.athen.api.profile.utils.ProfileParser
import foo.starred.athen.handlers.Beacon.request
import foo.starred.athen.utils.api

object ProfileAPI {
    fun get(
        username: String,
        inventory: Boolean = false,
        onSuccess: (PlayerProfileStats) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        get(listOf(username), inventory, { onSuccess(it.getValue(username)) }, onError)
    }

    fun get(
        usernames: Collection<String>,
        inventory: Boolean = false,
        onSuccess: (Map<String, PlayerProfileStats>) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        val names = usernames.joinToString(",")

        "hypixel?names=$names".api.request {
            if (inventory) headers("Include-Inventory" to "true")

            onSuccess<JsonArray> { array ->
                onSuccess(buildMap {
                    for (element in array) {
                        val obj = element.asJsonObject
                        val name = obj.get("first").asString
                        val json = obj.getAsJsonObject("second")

                        put(name, if (json.has("error")) PlayerProfileStats(loading = false) else ProfileParser.get(json))
                    }
                })
            }

            onError {
                Athen.LOGGER.error("Failed to batch fetch profile stats for $usernames", it)
                onError?.invoke(it) ?: onSuccess(usernames.associateWith { PlayerProfileStats(loading = false) })
            }
        }
    }
}