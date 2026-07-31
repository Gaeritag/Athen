package foo.starred.athen.api.rendering.ui.dsl.constraints.impl.size

import foo.starred.athen.api.rendering.ui.dsl.constraints.base.ISizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

class FixedSizeConstraint(val w: Int, val h: Int) : ISizeConstraint {
    override fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return w
    }

    override fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return h
    }
}