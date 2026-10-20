package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.modules.Module

@LoadableAthenModule
object CameraHelper : Module(
    "Camera helper",
    "QoL additions to the vanilla camera.",
    ConfigCategory.RENDER
) {
    private val _front by config.switch("No front camera", true)

    val front: Boolean
        get() = enabled && _front
}
