package foo.starred.athen.modules.impl.dungeon.partyfinder.enums

enum class PartyFinderClassType(short: String, val full: String, color: String) {
    ARCHER("A", "Archer", "<orange>"),
    BERSERK("B", "Berserk", "<red>"),
    HEALER("H", "Healer", "<pink>"),
    MAGE("M", "Mage", "<aqua>"),
    TANK("T", "Tank", "<dark_green>");

    val fancy = "$color$short"

    companion object {
        val all = entries.map { it.full }

        fun get(name: String): PartyFinderClassType? {
            return entries.firstOrNull { it.full == name }
        }
    }
}