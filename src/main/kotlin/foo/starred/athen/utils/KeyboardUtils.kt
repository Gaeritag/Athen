package foo.starred.athen.utils

import com.mojang.blaze3d.platform.InputConstants

val Int.keyName: String
    get() {
        return when (this) {
            -1 -> "None"
            in 0..7 -> "Mouse $this"
            else -> InputConstants.Type.KEYSYM.getOrCreate(this).displayName.string.let {
                if (it.length == 1) it.uppercase() else it
            }
        }
    }