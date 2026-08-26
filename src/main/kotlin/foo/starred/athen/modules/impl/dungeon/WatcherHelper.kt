@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.dungeon

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.dungeon.DungeonAPI
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.Category
import foo.starred.athen.config.dsl.impl.builders.hud.ConfigHudBuilder
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.PacketEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.level
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.alert
import foo.starred.snowbird.utils.stripped
import foo.starred.snowbird.utils.toDuration
import foo.starred.snowbird.utils.toDurationFromMillis
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import kotlin.math.abs

@Load
@OnlyIn(islands = [SkyBlockIsland.THE_CATACOMBS])
object WatcherHelper : Module(
    "Watcher helper",
    "Shows information about the watcher's speed and movements.",
    Category.DUNGEONS
) {
    private val breakdown by config.switch("Send breakdown", true)
    private val spawnedAll by config.switch("Show alert on all spawned", true)
    private val speak by config.switch("Show alert on speak", true)
    private val move by config.switch("Show alert on move", true)

    private val ex0 = listOf("Speak: §c23.4s §7(23.2s)", "Move: §c25.6s §7(24.6s)", "Total: §c57.3s §7(54.2s)").fcs
    private val ex1 = listOf("Speak: §c23.4s", "Move: §c25.6s", "Total: §c57.3s").fcs

    private val display = Ticking(2) {
        if (!DungeonAPI.bloodOpened.value) return@Ticking null
        if (DungeonAPI.inBoss.value) return@Ticking null

        buildString {
            append("Speak: §c$`display$speak`")
            if (showTicks) append(" §7($`display$speak$t`)")
            append('\n')

            append("Move: §c$`display$move`")
            if (showTicks) append(" §7($`display$move$t`)")
            append('\n')

            append("Total: §c$`display$total`")
            if (showTicks) append(" §7($`display$total$t`)")
        }.split("\n").fcs
    }

    private val hud: ConfigHudBuilder = config.hud("Blood timers") {
        if (it) return@hud if (showTicks) sizedText(ex0) else sizedText(ex1)
        sizedText(display.value ?: return@hud null)
    }

    private val showTicks by config.switch("Show ticks", true)

    private val alerts by config.group("Alert texts")
    private val `text$fast` by alerts.input("Fast", "<red>Vroom!")
    private val `text$normal` by alerts.input("Normal", "<red>Watcher!")
    private val `text$slow` by alerts.input("Slow", "<red>Yawn...")
    private val `text$snail` by alerts.input("Very slow", "<red>Zzz...")

    private val purreow = SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace("entity.cat.purreow"))

    private var `blood$start`: Long = 0
    private var `blood$start$t`: Int = 0
    private var `blood$speak`: Long = 0
    private var `blood$speak$t`: Int = 0
    private var `blood$move`: Long = 0
    private var `blood$move$t`: Int = 0

    private var `blood$watcher$x`: Double? = null
    private var `blood$watcher$z`: Double? = null
    private var `blood$watcher$moved`: Boolean = false

    private var `display$speak`: String = "???"
    private var `display$speak$t`: String = "???"
    private var `display$move`: String = "???"
    private var `display$move$t`: String = "???"
    private var `display$total`: String = "???"
    private var `display$total$t`: String = "???"

    private enum class Shrimp {
        SNAIL,
        SLOW,
        NORMAL,
        FAST;

        companion object {
            fun get(l: Long): Shrimp = when {
                l >= 25_000 -> SNAIL
                l >= 23_000 -> SLOW
                l >= 22_000 -> NORMAL
                else -> FAST
            }
        }
    }

    init {
        DungeonAPI.inBoss.onChange {
            if (!it) return@onChange
            resetStr()
        }

        DungeonAPI.bloodOpened.onChange {
            if (!it) return@onChange

            `blood$start` = System.currentTimeMillis()
            `blood$start$t` = Scheduler.ticks.server
        }

        DungeonAPI.bloodSpawnedAll.onChange {
            if (!it) return@onChange
            if (`blood$start` == 0L) return@onChange
            if (!spawnedAll) return@onChange
            if (!enabled) return@onChange

            val t = System.currentTimeMillis() - `blood$start`
            val t0 = Scheduler.ticks.server - `blood$start$t`

            val d = t.toDurationFromMillis(secondsDecimals = 1, secondsOnly = true)
            val d0 = (t0 / 20.0).toDuration(secondsDecimals = 1, secondsOnly = true)

            "Watcher took <red>$d <gray>($d0) <r>to spawn all!".mod()
        }

        DungeonAPI.bloodKilledAll.onChange {
            if (!it) return@onChange
            if (`blood$start` == 0L) return@onChange
            if (!enabled) return@onChange

            reset()

            if (!breakdown) return@onChange
            "Watcher time breakdown:".mod()
            " <gray>• <r>Speak time: <red>$`display$speak` <gray>| <red>$`display$speak$t`".mod()
            " <gray>• <r>Move time: <red>$`display$move` <gray>| <red>$`display$move$t`".mod()
            " <gray>• <r>Total time: <red>$`display$total` <gray>| <red>$`display$total$t`".mod()
        }

        on<LocationEvent.Server.Connect> {
            reset()
            resetStr()
        }

        on<TickEvent.Client.End> {
            if (DungeonAPI.bloodKilledAll.value) return@on

            `display$total` = (System.currentTimeMillis() - `blood$start`).toDurationFromMillis(secondsDecimals = 1, secondsOnly = true)
            `display$total$t` = ((Scheduler.ticks.server - `blood$start$t`) / 20.0).toDuration(secondsDecimals = 1, secondsOnly = true)
        }.runWhen(DungeonAPI.bloodOpened)

        on<MessageEvent.Chat.Receive> {
            if (stripped != "[BOSS] The Watcher: Let's see how you can handle this.") return@on
            if (`blood$start` == 0L) return@on

            `blood$speak` = System.currentTimeMillis()
            `blood$speak$t` = Scheduler.ticks.server

            val t = `blood$speak` - `blood$start`
            val t0 = `blood$speak$t` - `blood$start$t`

            `display$speak` = t.toDurationFromMillis(secondsDecimals = 1, secondsOnly = true)
            `display$speak$t` = (t0 / 20.0).toDuration(secondsDecimals = 1, secondsOnly = true)

            if (!speak) return@on

            val ty = Shrimp.get(t)

            "<hover:<red>$t0 <white>ticks.>Watcher took <red>$`display$speak` <gray>($`display$speak$t`)<r> to speak!".mod()

            when (ty) {
                Shrimp.FAST -> `text$fast`.parse().alert(subTitle = "Took <red>$`display$speak` <r>to speak!".parse(), soundType = purreow)
                Shrimp.NORMAL -> `text$normal`.parse().alert(subTitle = "Took <red>$`display$speak` <r>to speak!".parse(), soundType = purreow)
                Shrimp.SLOW -> `text$slow`.parse().alert(subTitle = "Took <red>$`display$speak` <r>to speak!".parse(), soundType = purreow)
                Shrimp.SNAIL -> `text$snail`.parse().alert(subTitle = "Took <red>$`display$speak` <r>to speak!".parse(), soundType = purreow)
            }
        }.runWhen(DungeonAPI.bloodOpened)

        on<PacketEvent.Receive, ClientboundMoveEntityPacket> {
            if (`blood$watcher$moved`) return@on
            if (!hasPosition()) return@on
            if (`blood$speak` == 0L) return@on
            if (System.currentTimeMillis() - `blood$speak` < 2500) return@on

            val l = level ?: return@on
            val e = getEntity(l)?.takeIf { it.customName?.stripped()?.contains("The Watcher") == true } ?: return@on
            val x = `blood$watcher$x` ?: e.x.also { `blood$watcher$x` = it }
            val z = `blood$watcher$z` ?: e.z.also { `blood$watcher$z` = it }

            if (abs(e.x - x) <= 0.05 && abs(e.z - z) <= 0.05) return@on

            `blood$move` = System.currentTimeMillis()
            `blood$move$t` = Scheduler.ticks.server
            `blood$watcher$moved` = true

            val t = `blood$move` - `blood$start`
            val t0 = `blood$move$t` - `blood$start$t`

            `display$move` = t.toDurationFromMillis(secondsDecimals = 1, secondsOnly = true)
            `display$move$t` = (t0 / 20.0).toDuration(secondsDecimals = 1, secondsOnly = true)

            if (!move) return@on

            "<hover:<red>$t0 <white>ticks.>Watcher moved at <red>$`display$move` <gray>($`display$move$t`)<r>!".mod()
        }.runWhen(DungeonAPI.bloodOpened)
    }

    private fun reset() {
        `blood$start` = 0
        `blood$start$t` = 0
        `blood$speak` = 0
        `blood$speak$t` = 0
        `blood$move` = 0
        `blood$move$t` = 0

        `blood$watcher$x` = null
        `blood$watcher$z` = null
        `blood$watcher$moved` = false
    }

    private fun resetStr() {
        `display$speak` = "???"
        `display$speak$t` = "???"
        `display$move` = "???"
        `display$move$t` = "???"
        `display$total` = "???"
        `display$total$t` = "???"
        display.reset()
    }
}
