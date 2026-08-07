package foo.starred.athen.config.modMenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import foo.starred.athen.config.ui.ClickGUI
import net.minecraft.client.gui.screens.Screen

internal object Impl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { _: Screen? -> ClickGUI }
}
