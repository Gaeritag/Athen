package foo.starred.athen.modules.impl.general.messageactions.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.general.messageactions.actions.IMessageAction
import foo.starred.athen.modules.impl.general.messageactions.actions.MessageActionType

@Load
object NoAction : IMessageAction {
    override val id = 0
    override val name = "None"
    override val serializable = ""

    override fun run() {}

    init {
        IMessageAction.register(MessageActionType(id, name) { NoAction })
    }
}