package foo.starred.athen.modules.impl.general.messageactions.data

data class ActionEntry(
    val pattern: String,
    val match: MatchType,
    val id: Int,
    val value: String,
    val enabled: Boolean = true,
    val category: String = "",
    val cancel: Boolean = false,
    val delay: Double = 0.0
)