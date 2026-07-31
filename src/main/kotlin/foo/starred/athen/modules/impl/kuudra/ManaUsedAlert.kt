package foo.starred.athen.modules.impl.kuudra

import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.config.Category
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.command
import foo.starred.snowbird.handlers.parser.parse

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object ManaUsedAlert : Module(
    "Mana used alert",
    "Alerts the party when you mana dump!",
    Category.KUUDRA
) {
    private val ignore0 by config.switch("Ignore if 0 players", true)
    private val regex = Regex("^Used Extreme Focus! \\((?<int>\\d+) Mana\\)$")

    init {
        on<MessageEvent.Chat.Receive> {
            val p = client.player ?: return@on
            val i0 = regex.findGroup(stripped, "int")?.toIntOrNull() ?: return@on
            var i1 = 0

            for (a in McLevel.players) {
                if (a == p) continue
                if (a.uuid.version() != 4) continue
                if (p.distanceToSqr(a) > 25) continue

                i1++
            }

            if (i1 == 0 && ignore0) return@on

            "pc $i0 mana used on $i1 players!".command(false)
            "<red>$i0 <r>mana used on <red>$i1 <r>players!".parse(true).modMessage()
        }
    }
}