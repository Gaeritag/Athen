package foo.starred.athen.api.kuudra

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.enums.*
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.events.*
import foo.starred.athen.events.core.on
import foo.starred.kbus.extensions.runWhen
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.abstraction.tablist.MinecraftTabList
import foo.starred.kontext.api.data.Observable
import foo.starred.kontext.api.entity.extension.serverMaxHealth
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.item.extension.get
import foo.starred.kontext.api.item.resolver.impl.TextureKontextItemDataResolver
import foo.starred.kontext.api.lazy.RefreshableLazy
import net.minecraft.world.entity.monster.Giant

//~ if >= 26.2 'monster.MagmaCube' -> 'monster.cubemob.MagmaCube'
import net.minecraft.world.entity.monster.MagmaCube

@LoadableAthenModule
object KuudraAPI {
    private val deathRegex = Regex("^ ☠ (?:(?<username>\\w+)|You were) .+(?: and became a ghost)?\\.$")
    private val tierRegex = Regex(" ⏣ Kuudra's Hollow \\(T(?<t>\\d+)\\)")
    private val completeRegex = Regex("^\\s+KUUDRA DOWN!")
    private val defeatRegex = Regex("^\\s+DEFEAT")
    private val buildRegex = Regex("Building Progress (?<progress>\\d+)% \\((?<players>\\d) Players Helping\\)")
    private val progressRegex = Regex("^PROGRESS: (?<progress>\\d+)%")
    private val eatRegex = Regex("^(?<user>\\w+) has been eaten by Kuudra!$")
    private val stunRegex = Regex("^\\w+ destroyed one of Kuudra's pods!$")
    private val supplyProgressRegex = Regex("^\\[[| ]+]\\s*(?<progress>\\d+)%$")

    private val set0 = setOf(KuudraSupply.supply, KuudraSupply.fuel)
    val set = setOf(KuudraPhase.Supply, KuudraPhase.Fuel)

    private val _k = RefreshableLazy(::fn) { !it.isAlive }

    @JvmStatic
    var buildProgress: Observable<Int> = Observable(0)
        private set

    @JvmStatic
    var buildPlayers: Int = 0
        private set

    @JvmStatic
    var inRun: Boolean = false
        private set

    @JvmStatic
    var tier: KuudraTier? = null
        private set

    @JvmStatic
    var phase: KuudraPhase? = null
        private set(value) {
            if (field == value) return
            field = value

            if (value == null) return
            value.event.post()
            KuudraEvent.Phase.Any(value).post()
        }

    @JvmStatic
    val kuudra: MagmaCube? by _k

    @JvmStatic
    val supplies: MutableSet<AbstractSupply> = mutableSetOf()

    @JvmStatic
    val fuels: MutableSet<AbstractSupply> = mutableSetOf()

    @JvmStatic
    val teammates: MutableSet<KuudraPlayer> = mutableSetOf()

    init {
        on<LocationEvent.Server.Connect> {
            reset()
        }

        on<KuudraEvent.Start> (priority = Int.MIN_VALUE) {
            teammates.clear()
            for (p in MinecraftTabList.players) teammates += KuudraPlayer(p.profile.name)
        }

        on<TickEvent.Client.End> {
            if (!inRun) return@on
            if (ticks % 2 != 0) return@on

            val player = client.player ?: return@on
            if (KuudraPhase.Skip.active && player.position().y < 10) phase = KuudraPhase.Kill.start()

            if (phase !in set) return@on
            if (phase == KuudraPhase.Supply) for (s in supplies) s.pos()
            else if (phase == KuudraPhase.Fuel) for (f in fuels) f.pos()

            if (ticks % 10 != 0) return@on
            val players = client.level?.players()?.takeIf { it.isNotEmpty() } ?: return@on

            if (phase == KuudraPhase.Supply) for (s in supplies) s.nearby = players.any { s.radAABB.contains(it.position()) }
            else if (phase == KuudraPhase.Fuel) for (f in fuels) f.nearby = players.any { f.radAABB.contains(it.position()) }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        // Elle sometimes does not send the end dialogue if another dialogue sequence is active,
        // that is when she is eaten by Kuudra and the run ends. Love hypixel and their bugs.
        on<EntityEvent.Update.Health> {
            if (!inRun) return@on
            if ((tier?.int ?: 0) < KuudraTier.BURNING.int) return@on
            if (phase != KuudraPhase.DPS) return@on
            if (entity != kuudra) return@on
            if (new <= 2f) phase = KuudraPhase.Kill.start()
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<EntityEvent.Update.Equipment> {
            if (!inRun) return@on
            val phase = phase ?: return@on

            if (phase.ordinal > KuudraPhase.Fuel.ordinal) return@on
            if (phase == KuudraPhase.Build && buildProgress.value < 90) return@on
            val e = entity as? Giant ?: return@on

            if (supplies.any { it.entity == e } || fuels.any { it.entity == e }) return@on
            if (e.mainHandItem.get<TextureKontextItemDataResolver, String>() !in set0) return@on

            val s = AbstractSupply(e)
            if (phase == KuudraPhase.Supply) supplies += s
            else fuels += s
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<EntityEvent.Unload> {
            if (!inRun) return@on
            if (phase !in set) return@on
            val e = entity as? Giant ?: return@on

            if (phase == KuudraPhase.Supply) supplies.removeIf { it.entity == e }
            else fuels.removeIf { it.entity == e }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<EntityEvent.Update.Named> {
            if (!inRun) return@on
            val phase = phase ?: return@on

            if (phase.ordinal > KuudraPhase.Build.ordinal) return@on
            val pos = entity.blockPosition()

            when (phase) {
                KuudraPhase.Supply -> {
                    when (stripped) {
                        "BRING SUPPLY CHEST HERE" -> {
                            KuudraSupply.at(pos)?.active = false
                        }

                        "✓ SUPPLIES RECEIVED ✓" -> {
                            KuudraSupply.at(pos)?.active = true
                        }
                    }
                }

                KuudraPhase.Build -> {
                    if (stripped == "PROGRESS: COMPLETE") {
                        KuudraSupply.at(pos, 100)?.built = true
                        return@on
                    }

                    progressRegex.find(stripped)?.let { match ->
                        val (progress) = match.destructured
                        KuudraSupply.at(pos, progress.toInt())?.built = false
                        return@on
                    }

                    buildRegex.find(stripped)?.let { match ->
                        val (p0, p1) = match.destructured
                        buildProgress.value = p0.toInt()
                        buildPlayers = p1.toInt()
                    }
                }

                else -> {}
            }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<ScoreboardEvent.Update> {
            if (tier != null) return@on

            for (line in added) {
                val (t) = tierRegex.find(line)?.destructured ?: continue
                tier = KuudraTier.get(t.toInt())
                break
            }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<MessageEvent.Chat.Receive>(Int.MIN_VALUE) {
            when {
                stripped == "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!" -> {
                    KuudraEvent.Start.post()
                    phase = KuudraPhase.Supply.start()
                    inRun = true
                }

                stripped == "[NPC] Elle: OMG! Great work collecting my supplies!" -> {
                    phase = KuudraPhase.Build.start()
                }

                stripped == "[NPC] Elle: Phew! The Ballista is finally ready! It should be strong enough to tank Kuudra's blows now!" -> {
                    phase = KuudraPhase.Fuel.start()
                }

                stripped == "[NPC] Elle: POW! SURELY THAT'S IT! I don't think he has any more in him!" -> {
                    phase = if (tier?.int in KuudraPhase.Skip.tiers) KuudraPhase.Skip.start() else KuudraPhase.Kill.start()
                }

                stripped == "You retrieved some of Elle's supplies from the Lava!" -> {
                    KuudraEvent.Supply.Pickup.post()
                }

                stripped == "You retrieved a Ballista Fuel Cell from the Lava!" -> {
                    KuudraEvent.Supply.Pickup.post()
                }

                stripped == "You moved and the Chest slipped out of your hands!" -> {
                    KuudraEvent.Supply.Drop.post()
                }

                completeRegex.matches(stripped) -> {
                    KuudraEvent.End.Success.post()
                    KuudraEvent.End.Any.post()
                    inRun = false
                }

                defeatRegex.matches(stripped) -> {
                    KuudraEvent.End.Defeat.post()
                    KuudraEvent.End.Any.post()
                    inRun = false
                }

                else -> {
                    deathRegex.find(stripped)?.let { match ->
                        val username = match.groups["username"]?.value
                        val n = username?.takeIf { it != "You" } ?: client.user.name

                        teammates.find { it.name == n }?.deaths++
                        return@on
                    }

                    val tier = tier ?: return@on
                    if (tier.int < KuudraTier.BURNING.int) return@on
                    if (KuudraPhase.Stun.started && KuudraPhase.DPS.started) return@on

                    if (!KuudraPhase.Stun.started) {
                        eatRegex.find(stripped)?.let { match ->
                            val (user) = match.destructured
                            if (user != "Elle") {
                                phase = KuudraPhase.Stun.start()
                            }
                            return@on
                        }
                    }

                    if (!KuudraPhase.DPS.started) {
                        if (stunRegex.containsMatchIn(stripped)) {
                            phase = KuudraPhase.DPS.start()
                            return@on
                        }
                    }
                }
            }
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)

        on<MessageEvent.Title.Main> {
            if (phase !in set) return@on
            val message = message.stripped().takeIf { it.isNotEmpty() } ?: return@on

            val (progress) = supplyProgressRegex.find(message)?.destructured ?: return@on
            if (KuudraEvent.Supply.Progress(progress.toInt(), message).post()) cancel()
        }.runWhen(PresetSkyBlockIsland.KUUDRA.state)
    }

    private fun fn(): MagmaCube? {
        return client.level?.entitiesForRendering()?.find { it is MagmaCube && it.size == 30 && it.serverMaxHealth == 100_000f } as? MagmaCube
    }

    private fun reset() {
        tier = null
        _k._v = null
        phase = null
        inRun = false

        buildProgress.value = 0
        buildPlayers = 0

        supplies.clear()
        fuels.clear()
        teammates.clear()
        KuudraSupply.reset()
        KuudraPhase.reset()
    }
}
