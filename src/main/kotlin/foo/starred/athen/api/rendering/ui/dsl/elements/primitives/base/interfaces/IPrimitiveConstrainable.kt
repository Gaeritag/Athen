@file:Suppress("Unused")

package foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.interfaces

import foo.starred.athen.api.rendering.ui.dsl.constraints.base.IPositionConstraint
import foo.starred.athen.api.rendering.ui.dsl.constraints.base.ISizeConstraint
import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

interface IPrimitiveConstrainable<T> : IPrimitiveSelf<T> where T : IPrimitiveElement<T> {
    var position: IPositionConstraint?
    var size: ISizeConstraint?

    fun constrain(parent: IPrimitiveElement<*>) {
        size?.let {
            self.width = it.width(self, parent)
            self.height = it.height(self, parent)
        }

        position?.let {
            self.x = it.x(self, parent)
            self.y = it.y(self, parent)
        }
    }
}