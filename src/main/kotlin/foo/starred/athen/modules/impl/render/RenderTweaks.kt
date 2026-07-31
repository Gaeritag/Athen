package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.modules.Module

@Load
object RenderTweaks : Module(
    "Render tweaks",
    "Tweaks Minecraft's rendering!",
    Category.RENDER
) {
    private val _nametag by config.switch("Show own nametag", true)

    @JvmStatic
    val nametag: Boolean
        get() = enabled && _nametag
}