package foo.starred.athen.modules.impl.dungeon.partyfinder.data

import foo.starred.athen.modules.impl.dungeon.partyfinder.enums.PartyFinderClassType
import foo.starred.athen.modules.impl.dungeon.partyfinder.enums.PartyFinderSlotStatus

data class PartyFinderSlotData(
    val floor: Int,
    val master: Boolean,
    val members: Set<Pair<String, PartyFinderClassType>>,
    val status: PartyFinderSlotStatus
)
