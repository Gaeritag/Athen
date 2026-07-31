package foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size

import foo.starred.athen.api.rendering.ui.dsl.constraints.base.ISizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

class PercentSizeConstraint(val w: Float, val h: Float) : ISizeConstraint {
    override fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return ((parent.width / 100f) * w).toInt()
    }

    override fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return ((parent.height / 100f) * h).toInt()
    }
}