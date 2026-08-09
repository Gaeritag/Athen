package foo.starred.athen.updater

import com.google.gson.JsonElement
import foo.starred.athen.Athen
import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.impl.Dev
import foo.starred.athen.utils.command
import foo.starred.snowbird.api.mainThread
import foo.starred.snowbird.handlers.time.client
import moe.nea.libautoupdate.CurrentVersion
import moe.nea.libautoupdate.PotentialUpdate
import moe.nea.libautoupdate.UpdateContext
import moe.nea.libautoupdate.UpdateTarget
import net.minecraft.SharedConstants
import java.util.concurrent.CompletableFuture

@Priority(-6)
object ModUpdater {
    private var skippedVersion: String by Dev.file.string("version")
    var trulySkip: String by Dev.file.string("trulySkipVersion")

    private val context = UpdateContext(
        ModrinthUpdateSource("avbpWn0t", SharedConstants.getCurrentVersion().name()),
        UpdateTarget.deleteAndSaveInTheSameFolder(Athen::class.java),
        current(),
        Athen.modId
    )

    init {
        context.cleanup()

        command {
            "checkupdate" {
                checkAndNotify(silent = false)
            }

            "checkupdate" / string("stream") {
                checkAndNotify(string("stream"), false)
            }

            "update" {
                installUpdate()
            }

            "update" / string("stream") {
                installUpdate(string("stream"))
            }
        }

        on<LocationEvent.Server.Connect> {
            Scheduler.schedule(60.client) { checkAndNotify() }
        }.once()
    }

    private fun checkForUpdate(stream: String = "release"): CompletableFuture<PotentialUpdate> {
        return context.checkUpdate(stream)
    }

    private fun checkAndNotify(stream: String = "release", silent: Boolean = true) {
        checkForUpdate(stream).thenAccept { update ->
            if (!silent && !update.isUpdateAvailable) return@thenAccept "No update available!".mod()
            if (!update.isUpdateAvailable) return@thenAccept

            val newVersion = update.update.versionName
            if (skippedVersion == newVersion && trulySkip == newVersion) return@thenAccept

            "Update available: $newVersion".mod()
            "Run /${Athen.modId} update to install".mod()

            if (newVersion == skippedVersion) return@thenAccept
            mainThread {
                UpdateGUI(Athen.modVersion, newVersion, onUpdate = { installUpdate(stream) }, onSkip = { skippedVersion = newVersion }, onRemind = {}).open()
            }
        }.exceptionally {
            Athen.LOGGER.error("Failed to check for updates: ${it.message}")
            null
        }
    }

    private fun installUpdate(stream: String = "release"): CompletableFuture<Boolean> {
        return checkForUpdate(stream).thenCompose { update ->
            if (!update.isUpdateAvailable) {
                "Already on latest version".mod(MessagePrefixType.ERROR)
                return@thenCompose CompletableFuture.completedFuture(false)
            }

            "Downloading update: ${update.update.versionName}".mod()
            update.launchUpdate().thenApply {
                "Update downloaded! Restart to apply.".mod(MessagePrefixType.SUCCESS)
                true
            }
        }.exceptionally {
            "Update failed: ${it.message}".mod(MessagePrefixType.ERROR)
            Athen.LOGGER.error("Failed to install update: ${it.message}")
            false
        }
    }

    private fun current() = object : CurrentVersion {
        override fun display() = Athen.modVersion

        override fun isOlderThan(element: JsonElement): Boolean {
            if (!element.isJsonPrimitive) return true

            fun String.parse() = removePrefix("v").split('.', '-').map { it.toIntOrNull() ?: 0 }

            val local = Athen.modVersion.parse()
            val remote = element.asString.parse()

            val maxLength = maxOf(local.size, remote.size)
            val l = local + List(maxLength - local.size) { 0 }
            val r = remote + List(maxLength - remote.size) { 0 }

            for (i in 0 until maxLength) {
                if (l[i] < r[i]) return true
                if (l[i] > r[i]) return false
            }

            return false
        }
    }
}