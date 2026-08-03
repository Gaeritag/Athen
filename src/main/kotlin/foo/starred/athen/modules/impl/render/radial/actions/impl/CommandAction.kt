@file:Suppress("ConstPropertyName")

package foo.starred.athen.modules.impl.render.radial.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.render.radial.actions.ActionType
import foo.starred.athen.modules.impl.render.radial.actions.IAction
import foo.starred.snowbird.api.command

@Load
class CommandAction(val command: String) : IAction {
    override val id: Int = int
    override val name: String = str
    override val serializable: String = command

    override fun run() {
        if (command.isEmpty()) return
        command.command()
    }

    companion object {
        const val int = 1
        const val str = "Command"

        init {
            IAction.register(ActionType(int, str) { CommandAction(it) })
        }
    }
}