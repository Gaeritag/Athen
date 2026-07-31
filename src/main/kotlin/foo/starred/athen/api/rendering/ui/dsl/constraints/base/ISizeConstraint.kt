package foo.starred.athen.api.rendering.ui.dsl.constraints.base

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

interface ISizeConstraint {
    fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int
    fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int
}