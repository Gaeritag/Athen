package foo.starred.athen.modules.impl.general.messageactions.actions

data class MessageActionType(
    val id: Int,
    val name: String,
    val fn: (String) -> IMessageAction
)