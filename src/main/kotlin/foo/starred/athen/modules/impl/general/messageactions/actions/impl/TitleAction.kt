@file:Suppress("ConstPropertyName")

package foo.starred.athen.modules.impl.general.messageactions.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.general.messageactions.actions.IMessageAction
import foo.starred.athen.modules.impl.general.messageactions.actions.MessageActionType
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.showTitle

@Load
class TitleAction(val text: String) : IMessageAction {
    private val parsed = text.parse()
    private val empty = text.isEmpty()

    override val id: Int = int
    override val name: String = str
    override val serializable: String = text

    override fun run() {
        if (empty) return
        parsed.showTitle(fadeIn = 10, stay = 70, fadeOut = 20)
    }

    companion object {
        const val int = 4
        const val str = "Title"

        init {
            IMessageAction.register(MessageActionType(int, str) { TitleAction(it) })
        }
    }
}