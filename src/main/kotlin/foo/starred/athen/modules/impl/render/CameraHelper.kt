package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.Load
import foo.starred.athen.config.Category
import foo.starred.athen.modules.Module

@Load
object CameraHelper : Module(
    "Camera helper",
    "QoL additions to the vanilla camera.",
    Category.RENDER
) {
    private val _front by config.switch("No front camera", true)

    val front: Boolean
        get() = enabled && _front
}