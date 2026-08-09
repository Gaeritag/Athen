package foo.starred.athen.api.messaging.impl

import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.athen.modules.impl.Dev
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.literal
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
        prefixType.component.copy().append(" ".literal()).append(colored).lie()
    }

    @JvmStatic
    fun String.dev() {
        if (Dev.debug) mod(MessagePrefixType.DEV)
    }

    @JvmStatic
    fun Component.dev() {
        if (Dev.debug) mod(MessagePrefixType.DEV)
    }
}