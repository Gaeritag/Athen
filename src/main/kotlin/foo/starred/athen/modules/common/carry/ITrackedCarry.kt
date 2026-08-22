package foo.starred.athen.modules.common.carry

import foo.starred.snowbird.utils.toDuration

interface ITrackedCarry {
    val player: String
    val type: String
    val short: String

    var total: Int
    var completed: Int
    var lastCompletionTime: Long
    var firstCompletionTime: Long

    fun str(): String {
        val now = System.currentTimeMillis()

        val timeSince = lastCompletionTime
            .takeIf { it != 0L }
            ?.let { ((now - it) / 1000.0).toDuration() }
            ?: "§7N/A"

        val rate = if (completed > 2 && firstCompletionTime != 0L) {
            val seconds = (now - firstCompletionTime) / 1000
            if (seconds > 0) "${completed * 3600 / seconds}/hr" else "§7N/A"
        } else "§7N/A"

        return "§7> §b$player §8[§7$short§8]§f: §b$completed§f/§b$total §7($timeSince | $rate)"
    }
}