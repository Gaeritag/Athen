package foo.starred.athen.api.location.island.impl

import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.api.location.island.base.ISkyBlockIsland
import foo.starred.kontext.api.data.Observable

enum class PresetSkyBlockIsland(val id: String, override val string: String): ISkyBlockIsland {
    NONE("none", "None"),

    PRIVATE_ISLAND("dynamic", "Private Island"),
    HUB("hub", "Hub"),
    DUNGEON_HUB("dungeon_hub", "Dungeon Hub"),
    THE_BARN("farming_1", "The Farming Islands"),
    THE_PARK("foraging_1", "The Park"),
    GOLD_MINES("mining_1", "Gold Mines"),
    DEEP_CAVERNS("mining_2", "Deep Caverns"),
    DWARVEN_MINES("mining_3", "Dwarven Mines"),
    CRYSTAL_HOLLOWS("crystal_hollows", "Crystal Hollows"),
    MINESHAFT("mineshaft", "Mineshaft"),
    SPIDERS_DEN("combat_1", "Spider's Den"),
    THE_END("combat_3", "The End"),
    CRIMSON_ISLE("crimson_isle", "Crimson Isle"),
    GARDEN("garden", "Garden"),
    BACKWATER_BAYOU("fishing_1", "Backwater Bayou"),
    LOTUS_ATOLL("lotus_atoll", "Lotus Atoll"),
    GALATEA("foraging_2", "Galatea"),
    TORRHUS_CANYON("foraging_3", "Torrhus Canyon"),
    SAFARI("safari", "Critter Safari"),

    THE_RIFT("rift", "The Rift"),
    DARK_AUCTION("dark_auction", "Dark Auction"),
    THE_CATACOMBS("dungeon", "The Catacombs"),
    KUUDRA("kuudra", "Kuudra"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop"),
    ;

    val state: Observable<Boolean> by lazy {
        LocationAPI.island.map { it == this }
    }

    val active: Boolean
        get() = LocationAPI.island.value == this

    override fun toString(): String {
        return string
    }

    companion object {
        fun of(key: String): PresetSkyBlockIsland? {
            return entries.firstOrNull { it.id == key }
        }
    }
}
