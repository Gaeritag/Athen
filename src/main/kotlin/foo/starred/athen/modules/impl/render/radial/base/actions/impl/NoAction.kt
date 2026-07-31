package foo.starred.athen.modules.impl.render.radial.base.actions.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.modules.impl.render.radial.base.actions.ActionType
import foo.starred.athen.modules.impl.render.radial.base.actions.IAction

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