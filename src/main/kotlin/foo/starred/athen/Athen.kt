@file:Suppress("ConstPropertyName")

package foo.starred.athen

import com.google.gson.Gson
import foo.starred.athen.annotations.AnnotationLoader
import foo.starred.athen.api.messaging.impl.MessagingAPI.dev
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.minecraft.mod.ModWrapper
import foo.starred.athen.api.network.http.WebAPI.request
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.Dev
import foo.starred.athen.utils.data
import foo.starred.kbus.impl.KBus
import foo.starred.snowbird.api.EMPTY_COMPONENT
import foo.starred.snowbird.api.center
import foo.starred.snowbird.api.lie
import foo.starred.snowbird.api.repeat
import foo.starred.snowbird.api.scheduling.scheduler.extensions.clientTicks
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.updater.logic.source.impl.ModrinthUpdateSource
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.time.Duration.Companion.hours

object Athen : ClientModInitializer {
    @JvmField
    val LOGGER: Logger = LogManager.getLogger(Athen::class.java)

    @JvmField
    val GSON: Gson = Gson()

    @JvmField
    val SCOPE: CoroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName(ModWrapper.name))

    @JvmField
    val BUS: KBus = KBus()

    override fun onInitializeClient() {
        InternalEvent.Mod.Loading.Start.post()
        AnnotationLoader.load()

        ModrinthUpdateSource("athen").init(ModWrapper.version)

        on<LocationEvent.Server.Connect> {
            Scheduler.schedule(20.clientTicks, ::install)
            Scheduler.schedule(60.clientTicks, ::broadcast)
            Scheduler.repeat(1.hours, action = ::broadcast)
        }.once()

        InternalEvent.Mod.Loading.End.post()
    }

    private fun install() {
        if (Dev.lastVersion == ModWrapper.version) return
        Dev.lastVersion = ModWrapper.version

        val divider = ("<dark_gray><strikethrough>" + "-".repeat()).parse()
        divider.lie()
        ("<${MochaColorScheme.Lavender.argb}>" + "Athen".center()).lie()
        divider.lie()
        "<gray>Thank you for installing Athen <dark_gray>(v${ModWrapper.version})<gray>.".lie()
        EMPTY_COMPONENT.lie()
        "<gray>Quick Start:".lie()
        "  <aqua>/athen config <gray>- Open configuration menu".lie()
        "  <aqua>/athen hud <gray>- Position HUD elements".lie()
        "  <aqua>/athen help <gray>- View all commands".lie()
        EMPTY_COMPONENT.lie()

        "<hover:<${MochaColorScheme.Lavender.argb}>Click to join!><click:url:${ModWrapper.discord}><gray>Need help? Click to join our Discord!".lie()

        divider.lie()
        "<gray><hover:<green>Click to open page!><click:url:https://patreon.com/starredskies>Want to help support the development for mods like Athen? Click here to open the Patreon :3".lie()
        divider.lie()
    }

    private fun broadcast() {
        "broadcast.txt".data.request {
            success<String> {
                val str = it.trim().takeIf { s -> s.isNotBlank() && s != Dev.lastBroadcast } ?: return@success

                "<hover:<${MochaColorScheme.Lavender.argb}>Broadcasted message!>$str".mod()
                Dev.lastBroadcast = str
            }

            error {
                "Failed to read broadcast: ${it.message}".dev()
            }
        }
    }
}
