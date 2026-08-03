@file:Suppress("ConstPropertyName")

package foo.starred.athen.modules.impl.render.radial.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.render.radial.actions.ActionType
import foo.starred.athen.modules.impl.render.radial.actions.IAction
import foo.starred.snowbird.api.message

@Load
class MessageAction(val message: String) : IAction {
    override val id: Int = int
    override val name: String = str
    override val serializable: String = message

    override fun run() {
        if (message.isEmpty()) return
        message.message()
    }

    companion object {
        const val int = 2
        const val str = "Message"

        init {
            IAction.register(ActionType(int, str) { MessageAction(it) })
        }
    }
}