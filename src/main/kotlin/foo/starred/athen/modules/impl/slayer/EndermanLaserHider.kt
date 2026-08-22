package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.config.Category
import foo.starred.athen.ducks.entity.EntityDuck.Companion.carry
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.modules.Module
import net.minecraft.world.entity.monster.EnderMan
import net.minecraft.world.entity.monster.Guardian
import kotlin.math.abs

@Load
@OnlyIn(islands = [SkyBlockIsland.THE_END])
object EndermanLaserHider : Module(
    "Enderman laser hider",
    "Hides the lasers for voidgloom bosses!",
    Category.SLAYER
) {
    private val carry by config.switch("Show for carries", true)
    private val set: MutableSet<EnderMan> = mutableSetOf()

    init {
        on<SlayerEvent.Boss.Spawn>(Int.MAX_VALUE) {
            if (entity !is EnderMan) return@on
            if (slayerInfo.owned) return@on
            if (!carry && entity.carry != null) return@on

            set.add(entity)
        }

        on<SlayerEvent.Boss.Death> {
            set.remove(entity)
        }

        on<TickEvent.Client.End> {
            if (ticks % 20 != 0) return@on
            set.removeIf { !it.isAlive }
        }

        on<LocationEvent.Server.Connect> {
            set.clear()
        }
    }

    @JvmStatic
    fun Guardian.fn(): Boolean {
        for (s in set) {
            if (abs(x - s.x) > 0.5) continue
            if (abs(z - s.z) > 0.5) continue
            if (abs(y - s.y) > 5) continue

            return true
        }

        return false
    }
}