package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.AthenModuleScope
import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.MessageEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.kontext.abstraction.chat.command.MinecraftCommands
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.color.alpha
import foo.starred.kontext.api.format.extension.time.parseDuration
import foo.starred.parallax.api.primitives.ParallaxCircle
import foo.starred.parallax.api.primitives.ParallaxText
import net.minecraft.world.phys.Vec3

@LoadableAthenModule
@AthenModuleScope(skyblock = true)
object IchorPool : Module(
    "Ichor pool",
    "Highlights the area on Ichor Pool",
    ConfigCategory.RENDER
) {
    private val onlyKuudra by config.switch("Only in Kuudra")
    private val notifyParty by config.switch("Notify party", true)
    private val prio by config.switch("Prioritize own", true)
    private val textColor by config.colorPicker("Text color", MochaColorScheme.Teal.argb)
    private val color by config.colorPicker("Circle color", MochaColorScheme.Sapphire.argb)

    private val messageRegex = Regex("^Party > (?:\\[[^]]*?] )?\\w{1,16}(?: [ቾ⚒])?: Ichor pool casted at (?<x>-?\\d+) (?<y>-?\\d+) (?<z>-?\\d+)")
    private var pos: Vec3? = null
    private var time: Long = 0

    init {
        on<LocationEvent.Server.Connect> {
            reset()
        }

        on<MessageEvent.Chat.Receive> {
            if (onlyKuudra && KuudraAPI.phase != KuudraPhase.Kill) return@on
            if (pos == null || !prio) {
                messageRegex.find(stripped)?.let {
                    val (x, y, z) = it.destructured
                    pos = Vec3(x.toDouble(), y.toDouble() + 0.1, z.toDouble())
                    time = System.currentTimeMillis()
                }
            }

            if (pos != null && !prio) return@on
            if ("Casting Spell: Ichor Pool!" != stripped) return@on

            val n = client.player?.blockPosition() ?: return@on
            //~ if >= 26.2 'n.center' -> 'Vec3.atCenterOf(n)'
            pos = n.center.add(0.0, 0.1, 0.0)
            time = System.currentTimeMillis()

            val str = "${n.x} ${n.y} ${n.z}"
            "Ichor pool casted at <red>$str".mod()
            if (notifyParty) {
                MinecraftCommands.client("pc Ichor pool casted at $str")
            }
        }

        on<WorldRenderEvent.Extract> {
            if (onlyKuudra && KuudraAPI.phase != KuudraPhase.Kill) return@on
            val center = pos ?: return@on
            val t = (20100 - (System.currentTimeMillis() - time)).takeIf { it > 0 } ?: return@on reset()

            ParallaxCircle.frame(center, 8.0, color)
            ParallaxCircle.fill(center, 8.0, color.alpha(0.5f))
            ParallaxText.string(t.div(1000).parseDuration(), center, textColor, depth = false, increase = true)
        }
    }

    private fun reset() {
        time = 0
        pos = null
    }
}
