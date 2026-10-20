package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import foo.starred.kontext.api.format.extension.string.stripped
import net.minecraft.network.chat.Component

sealed class MessageEvent {
    sealed class Chat {
        data class Intercept(val message: Component) : AthenEvent(), KBusCancellableTrait {
            val stripped = message.stripped()
        }

        data class Receive(val message: Component) : AthenEvent() {
            val stripped = message.stripped()
        }
    }

    sealed class Title {
        data class Main(val message: Component) : AthenEvent(), KBusCancellableTrait

        data class Sub(val message: Component) : AthenEvent(), KBusCancellableTrait
    }

    data class ActionBar(val message: Component) : AthenEvent()
}
