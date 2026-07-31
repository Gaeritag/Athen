package foo.starred.athen.api.rendering.ui.dsl.constraints.base

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

interface IPositionConstraint {
    fun x(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int
    fun y(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int
}