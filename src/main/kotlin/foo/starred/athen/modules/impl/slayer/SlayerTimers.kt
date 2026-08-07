package foo.starred.athen.modules.impl.slayer

import com.mojang.serialization.Codec
import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.config.Category
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.handlers.Chronos
import foo.starred.athen.handlers.Scribble
import foo.starred.athen.handlers.Texter.onHover
import foo.starred.athen.handlers.Typo.modMessage
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.command
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.repeat
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.stripped
import foo.starred.snowbird.utils.toDuration
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Load
@OnlyIn(skyblock = true)
object SlayerTimers : Module(
    "Slayer timers",
    "Kill and spawn timers for slayer bosses.",
    Category.SLAYER
) {
    private val scribble = Scribble("features/slayerTimers")
    private val killPBs = scribble.mutableMap("kill_pbs", Codec.STRING, Codec.DOUBLE)
    private var questStartTime: Long = 0
    private var startTick: Int = 0
    private var bool: Boolean = false

    init {
        on<SlayerEvent.Quest.Start> {
            questStartTime = System.currentTimeMillis()
        }

        on<SlayerEvent.Quest.End> {
            questStartTime = 0
        }

        on<SlayerEvent.Reset.Any> {
            reset()
        }

        on<SlayerEvent.Boss.Spawn> {
            if (!slayerInfo.owned) return@on

            val a = slayerInfo.type == SlayerBoss.Tarantula && slayerInfo.tier == SlayerTier.Five
            if (bool && a) return@on ::bool.set(false)
            if (a) bool = true

            startTick = Chronos.ticks.server
            val spawnTime = System.currentTimeMillis()
            if (questStartTime <= 0) return@on

            val time = (spawnTime - questStartTime) / 1000.0
            "Slayer spawned in <yellow>${time.toDuration(secondsDecimals = 1)}<r>.".parse().modMessage()
        }

        on<SlayerEvent.Boss.Death> {
            if (!slayerInfo.owned) return@on

            val time = entity.tickCount / 20.0
            val str0 = time.toDuration(secondsDecimals = 1)
            val time0 = Chronos.ticks.server - startTick
            val str1 = (time0 / 20.0).toDuration(secondsDecimals = 1)
            val key = slayerInfo.string + if (entity.customName?.stripped()?.contains("Conjoined Brood") == true) "_P2" else ""
            val pb = killPBs.value[key]

            val str = when {
                pb == null -> {
                    killPBs.update { this[key] = time }
                    "<yellow>$str0 <gray>| <yellow>$str1 <green>[New PB]"
                }

                time < pb -> {
                    killPBs.update { this[key] = time }
                    "<green>$str0 <gray>| <green>$str1 <dark_green>[-${(pb - time).toDuration(secondsDecimals = 1)}]"
                }

                else -> {
                    val a = time < pb * 1.1
                    val color = if (a) Mocha.Peach.argb else TextColor.RED
                    val tc = if (a) Mocha.Pink.argb else Mocha.Red.argb
                    "<$color>$str0 <gray>| <$color>$str1 <$tc>[+${(time - pb).toDuration(secondsDecimals = 1)}]"
                }
            }

            val a = slayerInfo.type == SlayerBoss.Tarantula && slayerInfo.tier == SlayerTier.Five
            val p = if (a && bool) " <dark_gray>[P1]<r>" else if (a) " <dark_gray>[P2]<r>" else ""
            "Slayer$p killed in $str<r>.".parse().onHover("<red>$time0 ticks.".parse()).modMessage()
        }

        command {
            "times" / "slayers" {
                val b0 = "<gray>${"-".repeat()}".parse()
                val b1 = "<dark_gray>${"-".repeat()}".parse()

                b0.lie()

                val a = killPBs.value.entries.groupBy { it.key.substringBeforeLast("_T") }
                var f = true

                for (type in a.keys.sorted()) {
                    if (!f) b1.lie()
                    f = false

                    "<aqua>✦ ${type.str()}".parse().lie()

                    val b = a[type]?.sortedBy { it.key } ?: return@invoke
                    for ((k, d) in b) {
                        val tier = k.substringAfterLast("_")
                        "  • <red>$tier<r>: <green>${d.toDuration(secondsDecimals = 1)}".parse().lie()
                    }
                }

                b0.lie()
            }
        }
    }

    private fun String.str(): String =
        lowercase().split("_").joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

    private fun reset() {
        bool = false
        questStartTime = 0
    }
}