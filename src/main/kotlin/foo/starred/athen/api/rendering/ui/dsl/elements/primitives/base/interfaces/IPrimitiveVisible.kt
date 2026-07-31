@file:Suppress("Unused")

package foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.interfaces

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

interface IPrimitiveVisible<T> : IPrimitiveSelf<T> where T : IPrimitiveElement<T> {
    var visible: Boolean
}