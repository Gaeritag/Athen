package foo.starred.athen.config.theme.base

import foo.starred.kontext.api.format.extension.color.alpha

interface IThemeColorScheme {
    val rgba: Int
    val argb: Int

    fun alpha(alpha: Float, rgba: Boolean = false): Int =
        if (rgba) this.rgba.alpha(alpha, rgba = true)
        else this.argb.alpha(alpha, rgba = false)
}
