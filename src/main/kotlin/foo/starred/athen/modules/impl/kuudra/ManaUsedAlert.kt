package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.text.parser.impl.parse

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.KUUDRA])
object ManaUsedAlert : Module(
    "Mana used alert",
    "Alerts the party when you mana dump!",
    ConfigCategory.KUUDRA
) {
    private val ignore0 by config.switch("Ignore if 0 players", true)
    private val regex = Regex("^Used Extreme Focus! \\((?<int>\\d+) Mana\\)$")

    init {
        on<MessageEvent.Chat.Receive> {
            val level = client.level ?: return@on
            val p = client.player ?: return@on
            val (i0Str) = regex.find(stripped)?.destructured ?: return@on
            val i0 = i0Str.toIntOrNull() ?: return@on
            var i1 = 0

            for (a in level.players()) {
                if (a == p) continue
                if (a.uuid.version() != 4) continue
                if (p.distanceToSqr(a) > 25) continue

                i1++
            }

            if (i1 == 0 && ignore0) return@on

            MinecraftMessages.display("pc $i0 mana used on $i1 players!")
            "<red>$i0 <r>mana used on <red>$i1 <r>players!".parse(true).mod()
        }
    }
}
