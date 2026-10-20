@file:Suppress("Unused", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.dungeon.partyfinder.impl

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.profile.ProfileAPI
import foo.starred.athen.api.profile.data.PlayerProfileStats
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.command
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.chat.generic.MinecraftChat
import foo.starred.kontext.abstraction.chat.message.MinecraftMessages
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.component.literal
import foo.starred.kontext.api.format.extension.number.format
import foo.starred.kontext.api.format.extension.number.unabbreviate
import foo.starred.kontext.api.format.extension.time.formatTime
import foo.starred.kontext.api.format.extension.time.unformatTime
import foo.starred.kontext.api.item.data.rarity.SkyBlockItemRarity
import foo.starred.kontext.api.scheduling.scheduler.extensions.serverTicks
import foo.starred.kontext.api.text.parser.impl.parse
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import kotlin.time.Duration.Companion.hours

@LoadableAthenModule
@AthenModuleScope(islands = [PresetSkyBlockIsland.DUNGEON_HUB])
object PartyFinderJoinStats : Module(
    "Party finder join stats",
    "Shows join stats for party finder! Allows you to auto-kick the player.",
    ConfigCategory.DUNGEONS
) {
    private val stats by config.switch("Stats on join")

    private val kick by config.switch("Auto kick")
    private val `kick$floor` by config.selector("Floor", listOf("F7", "M4", "M5", "M6", "M7"), 0)
    private val `kick$pb` by config.input("Required PB", placeholder = "5:30")
    private val `kick$secrets` by config.input("Required secrets", placeholder = "50k")
    private val `kick$secrets$average` by config.input("Required secret average", placeholder = "8.4")
    private val `kick$mp` by config.input("Required MP", placeholder = "800")
    private val `kick$message` by config.switch("Send kick message")
    private val `kick$message$party` by config.switch("Send to party")
    private val `kick$message$party$delay` by config.slider("Message send delay", 5, 1, 10, "ticks")

    private val regex = Regex("^Party Finder > (?:\\[.{1,7}])? ?(?<name>.{1,16}) joined the dungeon group! \\(.*\\)$")
    private val cache: Object2ObjectOpenHashMap<String, Pair<Long, PlayerProfileStats>> = Object2ObjectOpenHashMap()

    init {
        Scheduler.repeat(1.hours) {
            val i0 = System.currentTimeMillis() - 1.hours.inWholeMilliseconds
            val it = cache.object2ObjectEntrySet().fastIterator()

            while (it.hasNext()) if (it.next().value.first < i0) it.remove()
        }

        command {
            "stats" / word("name") {
                val name = string("name")
                val value = cache[name]
                if (value != null) return@word value.second.stats()

                ProfileAPI.get(name, true) { kv ->
                    cache[name] = Pair(System.currentTimeMillis(), kv)
                    kv.stats()
                }
            }
        }

        on<MessageEvent.Chat.Receive> {
            val (name) = regex.find(stripped)?.destructured ?: return@on
            if (name == client.user.name) return@on

            val value = cache[name]
            if (value != null) {
                if (stats) value.second.stats()
                if (kick) value.second.kick()
                return@on
            }

            ProfileAPI.get(name, true) { kv ->
                cache[name] = Pair(System.currentTimeMillis(), kv)
                if (stats) kv.stats()
                if (kick) kv.kick()
            }
        }
    }

    private fun PlayerProfileStats.kick() {
        val master = `kick$floor` > 0
        val pb = (if (master) dungeons?.`pbs$master` else dungeons?.`pbs$normal`)?.get(if (master) `kick$floor` + 3 else 7)

        val secrets = dungeons?.secrets ?: 0
        val average = dungeons?.`secrets$average` ?: 0.0
        val mp = inventory?.mp ?: 0

        val pb0 = `kick$pb`.takeIf { it.isNotBlank() }
        val pb1 = pb0?.unformatTime()?.toLong()
        val secrets0 = `kick$secrets`.takeIf { it.isNotBlank() }?.unabbreviate()
        val average0 = `kick$secrets$average`.takeIf { it.isNotBlank() }?.toDoubleOrNull()
        val mp0 = `kick$mp`.takeIf { it.isNotBlank() }?.unabbreviate()

        val reasons = mutableListOf<String>()

        pb1?.let {
            val pb2 = pb?.let { t -> (t / 1000).toInt() }
            if (pb2 == null || pb2 > it) reasons += "PB ${pb?.time() ?: "No S+"} > $pb0"
        }

        secrets0?.let {
            if (secrets < it) reasons += "Secrets $secrets < $it"
        }

        average0?.let {
            if (average < it) reasons += "Secret average: $average < $it"
        }

        mp0?.let {
            if (mp < it) reasons += "MP: $mp0 < $it"
        }

        if (reasons.isEmpty()) return
        MinecraftCommands.client("party kick $name")

        if (!`kick$message`) return
        val reason = reasons.joinToString(", ")
        "Kicked <aqua>$name<r>: $reason".mod()

        if (!`kick$message$party`) return
        Scheduler.schedule(`kick$message$party$delay`.serverTicks) {
            MinecraftCommands.client("party chat [Athen] Kicked $name: $reason")
        }
    }

    private fun PlayerProfileStats.stats() {
        val classes = dungeons?.classes
        val armor = inventory?.armor?.associateBy { it.i }

        val divider = ("<dark_gray><strikethrough>" + MinecraftChat.repeat("-")).parse()
        val main = MochaColorScheme.Green.argb
        val second = MochaColorScheme.Lavender.argb
        val third = MochaColorScheme.Red.argb

        MinecraftMessages.display(divider)
        "Stats for <aqua>$name<white>:".mod()
        MinecraftMessages.display(divider)
        MinecraftMessages.display(" <dark_gray>📈 <$main>Dungeons level: <bold><$second>${dungeons?.catacombs}")
        MinecraftMessages.display(" <dark_gray>✦ <$main>Class levels: <orange>A${classes?.get(ARCHER)} <dark_gray>| <red>B${classes?.get(BERSERK)} <dark_gray>| <pink>H${classes?.get(HEALER)} <dark_gray>| <aqua>M${classes?.get(MAGE)} <dark_gray>| <dark_green>T${classes?.get(TANK)}")
        MinecraftMessages.display(" <dark_gray>✪ <$main>Magical power: <bold><$second>${inventory?.mp?.format(false)}")
        MinecraftMessages.display(" <dark_gray>⚔ <$main>Total runs: <bold><$second>${dungeons?.total?.format(false)}")
        MinecraftMessages.display(" <dark_gray>✧ <$main>Secrets: <bold><$second>${dungeons?.secrets?.format(false)}</bold> [<hover:<$second>Secret average>${dungeons?.`secrets$average`?.format(false)}</hover>]")
        MinecraftMessages.display(" <dark_gray>☠ <$main>Blood mobs killed: <bold><$second>${dungeons?.blood?.format(false)}")
        MinecraftMessages.display(" <dark_gray>🐈 <$main>Active pet: <bold><${inventory?.pet?.rarity?.color}>${inventory?.pet?.name}")
        MinecraftMessages.display(divider)

        fun piece(a: Int, b: String): MutableComponent {
            return (armor?.get(a)?.let { it.name + (it.lore?.joinToString("\n")?.let { l -> "\n$l" } ?: "") } ?: "Empty").literal().let { a -> "<$third>[$b]".parse().apply { withStyle { style.withHoverEvent(HoverEvent.ShowText(a)) } } }
        }

        MinecraftMessages.display(
            " <dark_gray>▸ <$main>Armor: ".parse()
                .append(piece(3, "⛑")).append(" <dark_gray>| ".parse())
                .append(piece(2, "👕")).append(" <dark_gray>| ".parse())
                .append(piece(1, "👖")).append(" <dark_gray>| ".parse())
                .append(piece(0, "👢"))
        )

        val pb = ("<$main>Personal Bests\n" + (1..7).joinToString("\n") { f -> "<$main>F$f<dark_gray>: <$second>${dungeons?.`pbs$normal`?.get(f)?.time()} <dark_gray>| <$main>M$f<dark_gray>: <$second>${dungeons?.`pbs$master`?.get(f)?.time()}" }).parse()
        val pets = "<$main>Legendary and Mythic pets\n" + (inventory?.pets?.filter { it.rarity == SkyBlockItemRarity.LEGENDARY || it.rarity == SkyBlockItemRarity.MYTHIC }?.takeIf { it.isNotEmpty() }?.joinToString("\n") { "<${it.rarity.color}>${it.name}" } ?: "<white>No pets")
        MinecraftMessages.display(" <dark_gray>▸ <$third>[Personal Bests]".parse().apply { withStyle { style.withHoverEvent(HoverEvent.ShowText(pb)) } }.append(" <dark_gray>| <hover:$pets><$third>[Pets]".parse()))
        MinecraftMessages.display(divider)
    }

    private fun Long.time(): String {
        return (this / 1000.0).formatTime(false)
    }
}
