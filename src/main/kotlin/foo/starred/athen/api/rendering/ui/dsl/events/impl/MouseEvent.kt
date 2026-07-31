@file:Suppress("Unused")

package foo.starred.athen.api.rendering.ui.dsl.events.impl

import foo.starred.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement
import foo.starred.athen.api.rendering.ui.dsl.events.base.UIEvent

sealed class MouseEvent {
    data class Press(
        val x: Double,
        val y: Double,
        val button: Int,
        val element: IPrimitiveElement<*>
    ) : UIEvent()

    data class Release(
        val x: Double,
        val y: Double,
        val button: Int,
        val element: IPrimitiveElement<*>
    ) : UIEvent()

    data class Scroll(
        val x: Double,
        val y: Double,
        val amount: Double,
        val element: IPrimitiveElement<*>
    ) : UIEvent()

    sealed class Move {
        data class Any(
            val x: Double,
            val y: Double,
            val element: IPrimitiveElement<*>
        ) : UIEvent()

        data class Enter(
            val x: Double,
            val y: Double,
            val element: IPrimitiveElement<*>
        ) : UIEvent()

        data class Exit(
            val x: Double,
            val y: Double,
            val element: IPrimitiveElement<*>
        ) : UIEvent()
    }
}