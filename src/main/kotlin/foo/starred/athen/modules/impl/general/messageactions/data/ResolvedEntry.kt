package foo.starred.athen.modules.impl.general.messageactions.data

import foo.starred.athen.modules.impl.general.messageactions.actions.IMessageAction
import foo.starred.athen.utils.regex
import kotlin.time.Duration.Companion.seconds

class ResolvedEntry(src: ActionEntry) {
    private var last: MatchResult? = null

    val source = src.copy(pattern = src.pattern.trim())
    val groups = source.match == MatchType.REGEX && '$' in source.value

    val regex = if (source.match == MatchType.REGEX) source.pattern.regex() else null
    val action = IMessageAction.create(source.id, source.value)
    val delay = source.delay.seconds

    fun matches(text: String, set: Set<String>): Boolean {
        if (!source.enabled) return false
        if (source.category.isNotEmpty() && source.category in set) return false

        return when (source.match) {
            MatchType.CONTAINS -> text.contains(source.pattern, ignoreCase = true)
            MatchType.EXACT -> text == source.pattern
            MatchType.REGEX -> regex?.find(text).also { last = it } != null
        }
    }

    fun action(text: String): IMessageAction? {
        if (action == null) return null
        if (!groups) return action

        val r = last ?: return action
        var v = source.value

        for (i in r.groupValues.indices.reversed()) {
            if (i == 0) {
                v = text
                continue
            }

            v = v.replace("$$i", r.groupValues[i])
        }

        return IMessageAction.create(source.id, v)
    }
}
