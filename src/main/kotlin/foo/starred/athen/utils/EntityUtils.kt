package foo.starred.athen.utils

import foo.starred.snowbird.utils.stripped
import net.minecraft.network.chat.Component

private const val a = "Boomer|Flaming|Fortified|Golden|Healing|Healthy|Speedy|Stealth|Stormy"
private val b = Regex("""^(?:\[Lv\d+] )?(?:[^\w \-]+ )?(?:\[\w+] )?(?:(?:$a) )?(?<bool>.\s*Corrupted )?(?<name>.+?)(?: ᛤ)? \S*❤""")

val Component.name: String?
    get() {
        val a = stripped().takeIf { "❤" in it } ?: return null
        val b = b.find(a) ?: return null

        return b.groups["name"]?.value
            ?.let { if (b.groups["bool"] != null) it.dropLast(1) else it }
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }