package foo.starred.athen.api.slayers.data

import foo.starred.athen.api.slayers.enums.tier.SlayerTier
import foo.starred.athen.api.slayers.enums.type.base.ISlayerType
import foo.starred.kontext.abstraction.client.extension.client
import foo.starred.kontext.api.format.extension.string.stripped
import foo.starred.kontext.api.lazy.RefreshableLazy
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand

data class SlayerInfo(val entity: Entity) {
    private val _owner by RefreshableLazy(::fn0, true)
    private val _type by RefreshableLazy(::fn1, true)
    private val _tier by RefreshableLazy(::fn2, true)

    val serializable: String
        get() = "${type}_T${tier?.int}"

    val owned: Boolean
        get() = owner == client.user.name

    var owner: String? = null
        get() = field ?: _owner

    var type: ISlayerType? = null
        get() = field ?: _type

    var tier: SlayerTier? = null
        get() = field ?: _tier

    var phase: Int = 1

    override fun toString(): String {
        return "SlayerInfo(owned=$owned, type=$type, tier=$tier, age=${entity.tickCount / 20}s)"
    }

    private fun fn0(): String? {
        val stand = stand(3) ?: stand(4) ?: return null
        return stand.customName?.stripped()?.takeIf { spawnerRegex in it }?.substringBefore("'")?.trim()
    }

    private fun fn1(): ISlayerType? {
        val name = stand(1)?.customName?.stripped() ?: return null
        return ISlayerType.Companion.Names.map.entries.find { (a, _) -> name.contains(a) }?.value
    }

    private fun fn2(): SlayerTier? {
        val name = stand(1)?.customName?.stripped() ?: return null
        return SlayerTier.find(name)
    }

    private fun stand(offset: Int): ArmorStand? {
        return client.level?.getEntity(entity.id + offset) as? ArmorStand
    }

    companion object {
        private val spawnerRegex = Regex("'(?:s )?SLAYER BOSS")
    }
}
