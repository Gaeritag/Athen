@file:Suppress("UNUSED")

package foo.starred.athen.modules.impl

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.modules.Module

@Load
object ModSettings : Module(
    "Mod settings",
    "Toggles for a lot of the internal stuff in the mod!",
    Category.GENERAL
) {
    @JvmStatic
    val disableTickCulling by config.switch("Disable tick culling", true)

    private val _tickCullText by config.information("Disabling this may break slayer features!")

    @JvmStatic
    val commandConfig by config.switch("\'/athen\' opens config")

    @JvmStatic
    val upsideDown by config.switch("Upside down", true)

    @JvmStatic
    val priceFetch = config.slider("Price re-fetch", 10, 5, 60, "minutes").unique("priceFetch")

    @JvmStatic
    val hideGuis by config.switch("Hide GUIs in F1", true)

    @JvmStatic
    val calculator by config.switch("Enable \"/calc\"", true)

    private val _calculator by config.information("You will need to restart your game after toggling this option!")
}