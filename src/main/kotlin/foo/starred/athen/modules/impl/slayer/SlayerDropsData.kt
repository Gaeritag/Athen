@file:Suppress("Unused")

package foo.starred.athen.modules.impl.slayer

import com.mojang.serialization.Codec
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.slayers.enums.drop.base.ISlayerDrop
import foo.starred.athen.api.slayers.enums.drop.data.SlayerDropGrade
import foo.starred.athen.api.slayers.enums.drop.impl.*
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.api.storage.JsonStore
import foo.starred.athen.config.Category
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin
import foo.starred.snowbird.api.client
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.formatted
import foo.starred.snowbird.utils.stripped
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroups
import kotlin.math.min

@Load
@OnlyIn(skyblock = true)
object SlayerDropsData : Module(
    "Slayer drops data",
    "Shows useful data about your slayer drop chances!",
    Category.SLAYER
) {
    private val last by config.switch("Show chance on boss kill", true)
    private val _unused by config.information("This uses the last magic find from a boss drop to calculate the chances!")

    private val sinceLast by config.switch("Bosses since last drop", true)
    private val types by config.multiSelector("Stored types", SlayerDropGrade.entries.map { a -> a.name.lowercase().replaceFirstChar { it.uppercase() } })

    private val _filter by config.group("Filter")
    private val auto = _filter.switch("Detect automatically").unique("auto")
    private val _unused0 by _filter.information("You will need to change your selected RNG Meter item for it to be automatically detected.")
    private val rev by _filter.selector("Revenant", RevenantDrops.entries.map { it.display })
    private val tara by _filter.selector("Tarantula", TarantulaDrops.entries.map { it.display })
    private val sven by _filter.selector("Sven", SvenDrops.entries.map { it.display })
    private val void by _filter.selector("Voidgloom", VoidgloomDrops.entries.map { it.display })
    private val blaze by _filter.selector("Blaze", InfernoDrops.entries.map { it.display })

    private val json = JsonStore("features/slayerDropsData")
    private val map = json.mutableMap("map", Codec.STRING, Codec.INT)

    private val regex0 = Regex("^ {3}RNG Meter - (?<exp>[\\d,]+) Stored XP$")
    private val regex1 = Regex("^(?<type>RARE DROP!|VERY RARE DROP!|CRAZY RARE DROP!|INSANE DROP!) \\((?<name>.*?)\\) \\(\\+(?<mf>\\d+)% \uE01A Magic Find\\)$")
    private val regex2 = Regex("^You set your (?<boss>.*) RNG Meter to drop (?<item>.*)!")

    private var boss: SlayerBoss? = null
    private var tier: SlayerTier? = null
    private var xp: Long? = null
    private var mf: Int = 0

    init {
        on<SlayerEvent.Boss.Death> {
            if (!slayerInfo.owned) return@on
            val a = (slayerInfo.type as? SlayerBoss)?.takeIf { it != SlayerBoss.Vampire } ?: return@on

            boss = a
            tier = slayerInfo.tier

            if (slayerInfo.type == SlayerBoss.Tarantula && slayerInfo.tier == SlayerTier.Five && client.level?.getEntity(entity.id + 1)?.customName?.stripped()?.contains("Conjoined Brood") != true) return@on
            if (!sinceLast) return@on

            map.update {
                ISlayerDrop.ALL.filter { it.boss == a }.forEach {
                    val b = it.key()
                    this[b] = (this[b] ?: 0) + 1
                }
            }
        }

        on<MessageEvent.Chat.Receive> {
            val (a, b) = regex2.findGroups(stripped, "boss", "item") ?: return@on
            val a0 = a.boss ?: return@on
            val b0 = b.lowercase()

            ConfigManager.update(a0, (ISlayerDrop.Companion.Names.LOOKUP0[b0] as? Enum<*>)?.ordinal ?: return@on)
            "Changed selected drop for <red>$a <r>to <red>$b<r>!".mod()
        }.runWhen(auto.state)

        on<MessageEvent.Chat.Receive> {
            val (a, b, c) = regex1.findGroups(stripped, "type", "name", "mf") ?: return@on
            mf = c.toInt()

            var grade = SlayerDropGrade.entries.find { it.str == a } ?: return@on
            if (a == "VERY RARE DROP!") {
                val a0 = message.siblings.getOrNull(0)?.style?.color?.value ?: return@on
                grade = if (a0 == SlayerDropGrade.EXTRAORDINARY.color) SlayerDropGrade.EXTRAORDINARY else SlayerDropGrade.RARE
            }

            if (sinceLast) fn0(grade, b)
            if (last) return@on

            val l = (boss ?: SlayerBoss.Voidgloom).selected ?: return@on
            val i1 = xp ?: return@on
            val i2 = l.drop.xp ?: return@on
            val c0 = fn(i1, i2, l.drop.chance)
            "   <dark-gray>- <${Catppuccin.Mocha.Green.argb}>${l.display} <r>chance: <${Catppuccin.Mocha.Sky.argb}>${"%.5f".format(c0)} <dark_gray>[✯$mf]".parse().lie()
        }

        on<MessageEvent.Chat.Intercept> {
            if (!stripped.startsWith(" ")) return@on

            val l = (boss ?: SlayerBoss.Voidgloom).selected ?: return@on
            val t = (tier ?: SlayerTier.Four).xp

            val i0 = regex0.findGroup(stripped, "exp") ?: return@on
            val i1 = i0.replace(",", "").toLongOrNull()?.also { xp = it } ?: return@on
            val i2 = l.drop.xp ?: return@on

            val r0 = (i1.toDouble() / t).toInt()
            val r1 = (i2.toDouble() / t).toInt()
            val r2 = "%.2f".format(i1.toDouble() / i2.toDouble() * 100)

            cancel()
            "   <pink>RNG Meter <r>- <pink>$i0<gray>/<pink>${i2.formatted()} XP <dark_gray>[<gray>$r0<dark_gray>/<gray>$r1 <dark_gray>| <gray>$r2%<dark_gray>]".parse().lie()

            if (!last) return@on
            val c = fn(i1, i2, l.drop.chance)
            "   <dark_gray>- <${Catppuccin.Mocha.Green.argb}>${l.display} <r>chance: <${Catppuccin.Mocha.Sky.argb}>${"%.5f".format(c)}% <dark_gray>[✯$mf]".parse().lie()
        }
    }

    private fun fn(i1: Long, i2: Long, i3: Double): Double {
        if (i1 >= i2) return 100.0
        val a = 1.0 + min((2.0 * i1 / i2.toDouble()), 2.0)
        val b = i3 * a

        return if (b < 5.0) b * (1.0 + mf / 100.0) else b
    }

    private fun fn0(grade: SlayerDropGrade, b: String) {
        if (grade.ordinal !in types) return
        val b0 = ISlayerDrop.Companion.Names.LOOKUP0[b.lowercase()]?.key() ?: return
        val b1 = map.value[b0] ?: return map.update { this[b0] = 0 }

        "<red>$b1 <r>bosses since last <red>$b<r>!".parse(true).mod()
        map.update { this[b0] = 0 }
    }

    private fun ISlayerDrop.key(): String {
        val a = boss ?: return display2.lowercase()
        return "${a.display.lowercase().replace(" ", "_")}:${display2.lowercase().replace(" ", "_")}"
    }

    private val SlayerBoss.selected: ISlayerDrop?
        get() {
            return when (this) {
                SlayerBoss.Inferno -> InfernoDrops.entries.getOrNull(blaze)
                SlayerBoss.Revenant -> RevenantDrops.entries.getOrNull(rev)
                SlayerBoss.Sven -> SvenDrops.entries.getOrNull(sven)
                SlayerBoss.Tarantula -> TarantulaDrops.entries.getOrNull(tara)
                SlayerBoss.Voidgloom -> VoidgloomDrops.entries.getOrNull(void)
                else -> null
            }
        }

    private val String.boss: String?
        get() {
            return when (this) {
                SlayerBoss.Inferno.display -> "slayerDropsData.blaze"
                SlayerBoss.Revenant.display -> "slayerDropsData.rev"
                SlayerBoss.Sven.display -> "slayerDropsData.sven"
                SlayerBoss.Tarantula.display -> "slayerDropsData.tara"
                SlayerBoss.Voidgloom.display -> "slayerDropsData.void"
                else -> null
            }
        }

    private val ISlayerDrop.boss: SlayerBoss?
        get() {
            return when (this) {
                is RevenantDrops -> SlayerBoss.Revenant
                is TarantulaDrops -> SlayerBoss.Tarantula
                is SvenDrops -> SlayerBoss.Sven
                is VoidgloomDrops -> SlayerBoss.Voidgloom
                is InfernoDrops -> SlayerBoss.Inferno
                else -> null
            }
        }
}
