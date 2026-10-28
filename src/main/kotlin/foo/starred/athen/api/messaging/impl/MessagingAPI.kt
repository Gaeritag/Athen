package foo.starred.athen.api.messaging.impl

import foo.starred.athen.Athen
import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.text.parser.impl.parse
import net.minecraft.network.chat.Component

object MessagingAPI {
    @JvmStatic
    @JvmOverloads
    fun String.mod(prefixType: MessagePrefixType = MessagePrefixType.DEFAULT) {
        parse().mod(prefixType)
    }

    @JvmStatic
    @JvmOverloads
    fun Component.mod(prefixType: MessagePrefixType = MessagePrefixType.DEFAULT) {
        val colored = if (this.style.color == null) this.copy().withColor(0xFFFFFF) else this
        MinecraftMessages.display(prefixType.component.copy().append(" ".literal()).append(colored))
    }

    @JvmStatic
    fun String.dev() {
        if (Athen.Data.debug) mod(MessagePrefixType.DEV)
    }

    @JvmStatic
    fun Component.dev() {
        if (Athen.Data.debug) mod(MessagePrefixType.DEV)
    }
}
