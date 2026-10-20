package foo.starred.athen.api.slayers.enums.drop.data

import foo.starred.kontext.abstraction.chat.color.MinecraftColors

enum class SlayerDropGrade(val str: String? = null, val color: Int? = null) {
    GUARANTEED,
    OCCASIONAL("RARE DROP!", MinecraftColors.AQUA.color),
    RARE("VERY RARE DROP!", MinecraftColors.DARK_BLUE.color),
    EXTRAORDINARY("VERY RARE DROP!", MinecraftColors.DARK_PURPLE.color),
    PRAY_RNGESUS("CRAZY RARE DROP!", MinecraftColors.PINK.color),
    RNGESUS_INCARNATE("INSANE DROP!", MinecraftColors.RED.color);
}
