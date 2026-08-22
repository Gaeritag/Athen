package foo.starred.athen.modules.impl.general.messageactions.actions

interface IMessageAction {
    val id: Int
    val name: String
    val serializable: String

    fun run()

    companion object {
        private val registry = mutableMapOf<Int, MessageActionType>()

        fun register(a: MessageActionType) {
            registry[a.id] = a
        }

        fun create(id: Int, data: String): IMessageAction? =
            registry[id]?.fn?.invoke(data)

        fun all(): List<MessageActionType> =
            registry.values.sortedBy { it.id }
    }
}