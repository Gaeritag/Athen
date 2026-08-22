package foo.starred.athen.modules.impl.render.radial.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.render.radial.actions.ActionType
import foo.starred.athen.modules.impl.render.radial.actions.IAction

@Load
object NoAction : IAction {
    override val id: Int = 0
    override val name: String = "None"
    override val serializable: String = ""

    override fun run() {}

    init {
        IAction.register(ActionType(id, name) { NoAction })
    }
}