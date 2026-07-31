package foo.starred.athen.api.rendering.ui.dsl.constraints.impl.position

import foo.starred.athen.api.rendering.ui.dsl.constraints.base.IPositionConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

class FixedPositionConstraint(val x: Int, val y: Int) : IPositionConstraint {
    override fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return parent.x + x
    }

    override fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return parent.y + y
    }
}