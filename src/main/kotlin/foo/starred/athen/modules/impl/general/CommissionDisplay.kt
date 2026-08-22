@file:Suppress("Unused", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.rendering.ui.text.vanilla.extensions.sizedText
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.Category
import foo.starred.athen.modules.Module
import foo.starred.athen.ui.themes.Catppuccin.Mocha
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.handlers.parser.parse
import foo.starred.snowbird.utils.formatted
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.api.area.mining.Commission
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI

@Load
@OnlyIn(islands = [SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.MINESHAFT, SkyBlockIsland.CRYSTAL_HOLLOWS])
object CommissionDisplay : Module(
    "Commission display",
    "Displays your commissions without you having to open the tab menu!",
    Category.GENERAL
) {
    private val titleStyle = config.input("General title", "<red>Commissions:").unique("titleStyle")
    private val noneStyle = config.input("None available text", "<red>No commissions available!").unique("noneStyle")
    private val commissionStyle by config.input("Commission text", "<gray>- <r>#name: #progress")
    private val `commissionStyle$perc` by config.switch("Colored percent", true)
    private val unused by config.variables("#name", "#progress")

    private val ex0 = listOf("§cCommissions:", "§7- §fExample: §640%", "§7- §fExample: §e70%", "§7- §fExample: §c7%").fcs

    private var fcs0 = noneStyle.value.parse().visualOrderText
    private var fcs1 = titleStyle.value.parse().visualOrderText
    private val display = Ticking(20) {
        val area = CommissionArea.currentArea ?: return@Ticking listOf(fcs1, fcs0)
        val commissions = CommissionsAPI.commissions.filter { it.area == area }.takeIf { it.isNotEmpty() } ?: return@Ticking listOf(fcs1, fcs0)

        buildList {
            add(fcs1)
            for (c in commissions) add(c.prs())
        }
    }

    init {
        noneStyle.state.onChange { fcs0 = it.parse().visualOrderText }
        titleStyle.state.onChange { fcs1 = it.parse().visualOrderText }

        config.hud("Commission display") {
            if (it) return@hud sizedText(ex0)
            sizedText(display.value ?: return@hud null)
        }
    }

    private fun Commission.prs(): FormattedCharSequence {
        val p = (progress * 100f).coerceIn(0f, 100f)
        val c = when {
            !`commissionStyle$perc` -> ""
            p >= 100f -> "<${Mocha.Green.argb}>"
            p >= 75f -> "<${Mocha.Teal.argb}>"
            p >= 50f -> "<yellow>"
            p >= 25f -> "<gold>"
            else -> "<red>"
        }

        return commissionStyle
            .replace("#name", name)
            .replace("#progress", "$c${p.formatted()}%")
            .parse(true)
            .visualOrderText
    }
}