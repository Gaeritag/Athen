@file:Suppress("Unused")

package foo.starred.athen.config.theme.impl.rosepine

import foo.starred.athen.config.theme.base.IThemeColorScheme
import foo.starred.kontext.api.format.impl.color.ColorFormatter.rgba

enum class RosePineColorScheme(override val rgba: Int) : IThemeColorScheme {
    Base(rgba(35, 33, 54)),
    Surface(rgba(42, 39, 63)),
    Overlay(rgba(57, 53, 82)),
    Muted(rgba(110, 106, 134)),
    Subtle(rgba(144, 140, 170)),
    Text(rgba(224, 222, 244)),
    Love(rgba(235, 111, 146)),
    Gold(rgba(246, 193, 119)),
    Rose(rgba(234, 154, 151)),
    Pine(rgba(62, 143, 176)),
    Foam(rgba(156, 207, 216)),
    Iris(rgba(196, 167, 231)),
    HighlightLow(rgba(42, 40, 62)),
    HighlightMed(rgba(68, 65, 90)),
    HighlightHigh(rgba(86, 82, 110));

    override val argb: Int =
        (rgba ushr 8) or (rgba shl 24)
}
