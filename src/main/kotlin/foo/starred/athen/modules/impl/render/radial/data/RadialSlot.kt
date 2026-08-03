package foo.starred.athen.modules.impl.render.radial.data

import foo.starred.athen.modules.impl.render.radial.actions.IAction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.utils.extentions.createSkull

class RadialSlot(
    var name: String,
    var at: Int = 0,
    var av: String = "",
    var sub: List<RadialSlot> = emptyList(),
    itemId: String = "barrier",
    text: String? = null,
) {
    @Transient
    private var _item: ItemStack? = null

    @Transient
    private var _action: IAction? = null

    var action: IAction
        get() {
            _action?.let { return it }
            return IAction.create(at, av).also { _action = it }
        }
        set(value) {
            at = value.id
            av = value.serializable
            _action = value
        }

    var itemId: String = itemId
        set(value) {
            if (field == value) return
            field = value
            _item = null
        }

    var text: String? = text
        set(value) {
            if (field == value) return
            field = value
            _item = null
        }

    val item: ItemStack
        get() {
            _item?.let { return it }
            val r = runCatching { BuiltInRegistries.ITEM.getOptional(Identifier.withDefaultNamespace(itemId)).orElse(Items.BARRIER) }.getOrDefault(Items.BARRIER)
            val t = text
            return (if (r == Items.PLAYER_HEAD && t != null) createSkull(t) else r.defaultInstance).also { _item = it }
        }

    fun clone(): RadialSlot {
        return RadialSlot(name, at, av, sub.map { it.clone() }.toMutableList(), itemId, text)
    }
}