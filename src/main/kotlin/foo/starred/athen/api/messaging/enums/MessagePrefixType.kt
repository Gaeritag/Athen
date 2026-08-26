package foo.starred.athen.api.messaging.enums

import foo.starred.snowbird.api.text.parser.impl.parse
import net.minecraft.network.chat.Component

enum class MessagePrefixType(val component: Component) {
    DEFAULT("<#FFBCE2>[<#FDCCDA>A<#FCDDD3>t<#FAEDCB>h<#F0E2D7>e<#E5D8E4>n<#DBCDF0>]".parse()),
    SUCCESS("<#C5F3FF>[<#ACF0FF>A<#92EEFF>t<#79EBFF>h<#92EEFF>e<#ACF0FF>n<#C5F3FF>]".parse()),
    ERROR("<#E48296>[<#ED5775>A<#F62B55>t<#FF0034>h<#F62B55>e<#ED5775>n<#E48296>]".parse()),
    DEV("<#317F94>[<#316C7C>A<#315A64>t<#31474C>h<#2D5359>e<#2A5E67>n<#266A74>]".parse())
}
