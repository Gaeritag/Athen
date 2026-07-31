package foo.starred.athen.modules.impl.general.messageactions.data

import foo.starred.athen.ui.base.ICategoryEntry

data class CategoryEntry(
    override val name: String,
    override val enabled: Boolean = true
) : ICategoryEntry