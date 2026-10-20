@file:Suppress("Unused")

package foo.starred.athen.config.theme.impl.catppuccin

import foo.starred.athen.config.theme.base.IThemeColorScheme
import foo.starred.kontext.api.format.impl.color.ColorFormatter.rgba

enum class MochaColorScheme(override val rgba: Int) : IThemeColorScheme {
    Rosewater(rgba(245, 224, 220)),
    Flamingo(rgba(242, 205, 205)),
    Pink(rgba(245, 194, 231)),
    Mauve(rgba(203, 166, 247)),
    Red(rgba(243, 139, 168)),
    Maroon(rgba(235, 160, 172)),
    Peach(rgba(250, 179, 135)),
    Yellow(rgba(249, 226, 175)),
    Green(rgba(166, 227, 161)),
    Teal(rgba(148, 226, 213)),
    Sky(rgba(137, 220, 235)),
    Sapphire(rgba(116, 199, 236)),
    Blue(rgba(137, 180, 250)),
    Lavender(rgba(180, 190, 254)),
    Text(rgba(205, 214, 244)),
    Subtext1(rgba(186, 194, 222)),
    Subtext0(rgba(166, 173, 200)),
    Overlay2(rgba(147, 153, 178)),
    Overlay1(rgba(127, 132, 156)),
    Overlay0(rgba(108, 112, 134)),
    Surface2(rgba(88, 91, 112)),
    Surface1(rgba(69, 71, 90)),
    Surface0(rgba(49, 50, 68)),
    Base(rgba(30, 30, 46)),
    Mantle(rgba(24, 24, 37)),
    Crust(rgba(17, 17, 27));

    override val argb: Int =
        (rgba ushr 8) or (rgba shl 24)
}
