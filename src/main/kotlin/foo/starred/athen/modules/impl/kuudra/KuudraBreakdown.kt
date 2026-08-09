package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraTier
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.Category
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.regex
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.name
import foo.starred.snowbird.handlers.parser.parse
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object KuudraBreakdown : Module(
    "Kuudra breakdown",
    "Sends a message about what each player did at the end of the run.",
    Category.KUUDRA
) {
    private val freshMessage = config.textInput("Fresh regex", "FRESH.*").custom("freshRegex")
    private var freshRegex: Regex? = null

    private val supplyRegex = Regex("(?:\\[[^]]*] )?(?<user>\\w+) recovered one of Elle's supplies! \\(\\d+/\\d+\\)")
    private val fuelRegex = Regex("(?:\\[[^]]*] )?(?<user>\\w+) recovered a Fuel Cell and charged the Ballista! \\(\\d+%\\)")
    private val stunRegex = Regex("(?<user>\\w+) destroyed one of Kuudra's pods!")
    private val partyRegex = Regex("^Party > (?:\\[[^]]*?] )?(?<username>\\w{1,16})(?: [ቾ⚒])?: ?(?<message>.+)$")

    private val set = mutableSetOf<Player>()

    init {
        freshRegex = freshMessage.value.regex()

        freshMessage.state.onChange {
            freshRegex = it.regex() ?: return@onChange
        }

        on<KuudraEvent.Start> {
            set.clear()
            for (t in KuudraAPI.teammates) set.add(Player(t.name))
        }

        on<MessageEvent.Chat.Receive> {
            if (stripped.isEmpty()) return@on

            if (stripped == "[NPC] Elle: Good job everyone. A hard fought battle come to an end. Let's get out of here before we run into any more trouble!") {
                if (set.isEmpty()) return@on

                val fresh: MutableList<String> = mutableListOf()

                "<red>Run breakdown:".mod()
                for (p in set) {
                    if (p.fresh > 0) fresh.add("<red>${p.name}<white>: ${p.fresh}")

                    val a = " • <yellow>${p.name} <gray>- <red>${p.supply} <r>Supplies <gray>| <red>${p.fuel} <r>Fuels <gray>| <red>${p.deaths ?: "???"} <r>Deaths"
                    val b = "<hover:<red>${p.stun} <r>Stuns>"

                    if (p.stun > 0) "$b$a".parse().lie() else a.parse().lie()
                }

                val total = set.sumOf { it.fresh }

                if (fresh.isNotEmpty()) " • <hover:${fresh.joinToString("\n")}><orange>Freshens: <red>$total".parse().lie()
                else " • <orange>Freshens: <red>$total".parse().lie()

                return@on
            }

            if (stripped == "Your Fresh Tools Perk bonus doubles your building speed for the next 10 seconds!") {
                set.find { it.name == name }?.fresh++
                return@on
            }

            partyRegex.findThenNull(stripped, "username", "message") { (username, message) ->
                if (username == name) return@findThenNull
                if (freshRegex?.matches(message) != true) return@findThenNull
                set.find { it.name == username }?.fresh++
            } ?: return@on

            supplyRegex.findThenNull(stripped, "user") { (user) ->
                set.find { it.name == user }?.supply++
            } ?: return@on

            fuelRegex.findThenNull(stripped, "user") { (user) ->
                set.find { it.name == user }?.fuel++
            } ?: return@on

            val tier = KuudraAPI.tier?.int ?: return@on
            if (tier < KuudraTier.BURNING.int) return@on

            stunRegex.findThenNull(stripped, "user") { (user) ->
                set.find { it.name == user }?.stun++
            }
        }
    }

    private data class Player(
        val name: String,
        var supply: Int = 0,
        var fuel: Int = 0,
        var stun: Int = 0,
        var fresh: Int = 0
    ) {
        val deaths: Int?
            get() = KuudraAPI.teammates.find { it.name == name }?.deaths
    }
}