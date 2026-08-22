package foo.starred.athen.api.slayers.data

import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.base.ISlayerType
import foo.starred.snowbird.api.level
import foo.starred.snowbird.api.name
import foo.starred.snowbird.handlers.delegate.Expirable
import foo.starred.snowbird.utils.stripped
import net.minecraft.world.entity.Entity

data class SlayerInfo(val entity: Entity) {
    val owner by Expirable(::fn0, true)
    val type by Expirable(::fn1, true)
    val tier by Expirable(::fn2, true)

    val string: String
        get() = "${type}_T${tier?.int}"

    val owned: Boolean
        get() = owner == name

    private fun fn0(): String? {
        return level?.getEntity(entity.id + 3)?.customName?.stripped()?.substringAfterLast(":")?.trim()
    }

    private fun fn1(): ISlayerType? {
        val name = level?.getEntity(entity.id + 1)?.customName?.stripped() ?: return null
        return ISlayerType.Companion.Names.map.entries.find { (a, _) -> name.contains(a) }?.value
    }

    private fun fn2(): SlayerTier? {
        return SlayerTier.find(level?.getEntity(entity.id + 1)?.customName?.stripped() ?: return null)
    }

    override fun toString(): String {
        return "SlayerInfo(owner=$owner, isOwnedByPlayer=$owned, type=$type, tier=$tier, age=${entity.tickCount / 20}s)"
    }
}