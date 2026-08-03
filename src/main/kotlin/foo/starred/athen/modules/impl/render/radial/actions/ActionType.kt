package foo.starred.athen.modules.impl.render.radial.actions

data class ActionType(
    val id: Int,
    val name: String,
    val fn: (String) -> IAction
)