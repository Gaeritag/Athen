@file:Suppress("ObjectPrivatePropertyName", "Unused")

package foo.starred.athen.modules.impl.render.radial.ui.editor

import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.position.CenterPositionConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.position.FixedPositionConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size.FillSizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size.FixedSizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size.MixedSizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size.PercentSizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl.ContainerPrimitive.Companion.container
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl.RectanglePrimitive.Companion.rectangle
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.impl.ScrollablePrimitive.Companion.scrollable
import foo.starred.athen.api.rendering.ui.dsl.screen.PrimitiveScreen
import foo.starred.athen.modules.impl.render.radial.RadialMenu
import foo.starred.athen.modules.impl.render.radial.actions.IAction
import foo.starred.athen.modules.impl.render.radial.data.RadialSlot
import foo.starred.athen.modules.impl.render.radial.ui.components.RadialForm
import foo.starred.athen.modules.impl.render.radial.ui.components.RadialHeader
import foo.starred.athen.modules.impl.render.radial.ui.components.RadialPreview
import foo.starred.athen.modules.impl.render.radial.ui.components.RadialTree
import foo.starred.athen.ui.themes.Catppuccin.Mocha

object RadialEditor : PrimitiveScreen("Radial Menu Editor [Athen]") {
    private var head: RadialHeader
    private var tree: RadialTree
    private var form: RadialForm
    private var view: RadialPreview

    val working = mutableListOf<RadialSlot>()
    val collapsed = mutableSetOf<Int>()

    var main = 0
    var sub = -1
    var editing = false
    var type = 0

    val slot: RadialSlot?
        get() {
            if (sub >= 0) return working.getOrNull(main)?.sub?.getOrNull(sub)
            return working.getOrNull(main)
        }

    val names: List<String>
        get() {
            return RadialMenu.configs.keys.toList()
        }

    val max: Int
        get() {
            return when (RadialMenu.type) {
                1 -> 5
                2 -> working.size
                else -> Int.MAX_VALUE
            }
        }

    init {
        container {
            size = FillSizeConstraint()
            position = FixedPositionConstraint(0, 0)
            interact = false
            attach(scene)
        }

        val main = container {
            size = FixedSizeConstraint(906, 320)
            position = CenterPositionConstraint()
            attach(scene)
        }

        val side = rectangle {
            size = FixedSizeConstraint(130, 320)
            position = FixedPositionConstraint(0, 0)
            color = Mocha.Base.argb
            border = true
            borderColor = Mocha.Surface0.argb
            attach(main)
        }

        head = RadialHeader(side)

        tree = RadialTree(scrollable {
            size = MixedSizeConstraint(PercentSizeConstraint(100f, 0f), FixedSizeConstraint(0, 296))
            position = FixedPositionConstraint(0, 24)
            attach(side)
        })

        form = RadialForm(container {
            size = FixedSizeConstraint(444, 320)
            position = FixedPositionConstraint(136, 0)
            attach(main)
        })

        view = RadialPreview(main)
    }

    override fun init() {
        super.init()
        working.clear()
        working.addAll(RadialMenu.slots)
        collapsed.clear()
        reload(0, -1)
    }

    override fun onClose() {
        commit()
        RadialMenu.slots.clear()
        RadialMenu.slots.addAll(working)
        RadialMenu.save()
        RadialMenu.disk()
        super.onClose()
    }

    fun extra(): List<Pair<Int, RadialSlot>> {
        if (RadialMenu.type != 2) return emptyList()
        if (main !in working.indices) return emptyList()

        val list0 = working[main].sub
        val n = list0.size
        val m = maxOf(3, working.size)
        val p = main

        return List(n) { i ->
            ((p - n / 2 + i + m * 2) % m) to list0[i]
        }
    }

    fun reload(m0: Int = main, s0: Int = sub) {
        main = m0.coerceIn(0, maxOf(0, working.size - 1))
        sub = if (s0 >= 0) s0.coerceIn(0, maxOf(0, (working.getOrNull(main)?.sub?.size ?: 1) - 1)) else -1
        unfocus()

        val s = slot ?: run {
            head.fn()
            tree.fn()
            form.fn()
            return
        }

        form.name.value = s.name
        form.name.cursor = s.name.length
        form.item.value = s.itemId
        form.item.cursor = s.itemId.length

        type = s.action.id
        form.value.value = s.action.serializable
        form.value.cursor = s.action.serializable.length

        val tex = s.text ?: ""
        form.texture.value = tex
        form.texture.cursor = tex.length

        head.fn()
        tree.fn()
        form.fn()
    }

    fun commit() {
        val s = slot ?: return
        s.name = form.name.value
        s.itemId = form.item.value
        s.text = form.texture.value.ifBlank { null }
        s.action = IAction.create(type, form.value.value)
    }

    fun unfocus() {
        scene.focused = null
        editing = false
    }

    fun rename() {
        val n = head.field0.value.trim()
        if (n.isBlank() || n == RadialMenu.active || n in names) {
            editing = false
            head.fn()
            return
        }

        RadialMenu.rename(RadialMenu.active, n)
        editing = false
        head.fn()
    }

    fun save() {
        commit()
        RadialMenu.slots.clear()
        RadialMenu.slots.addAll(working)
        RadialMenu.save()
    }

    fun switch(name: String) {
        RadialMenu.load(name)
        working.clear()
        working.addAll(RadialMenu.slots)
        collapsed.clear()
        reload(0, -1)
    }
}