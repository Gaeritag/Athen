package foo.starred.athen.api.rendering.ui.dsl.constraints.impl.position

import foo.starred.athen.api.rendering.ui.dsl.constraints.base.IPositionConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

class MixedPositionConstraint(val x: IPositionConstraint, val y: IPositionConstraint) : IPositionConstraint {
    override fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return x.x(element, parent)
    }

    override fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return y.y(element, parent)
    }
}