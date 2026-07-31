package foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.interfaces

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

interface IPrimitiveSelf<T : IPrimitiveElement<T>> {
    val self: T
}