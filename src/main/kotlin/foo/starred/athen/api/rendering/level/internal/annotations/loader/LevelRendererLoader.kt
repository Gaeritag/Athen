package foo.starred.athen.api.rendering.level.internal.annotations.loader

import foo.starred.athen.annotations.Priority
import foo.starred.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import foo.starred.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import foo.starred.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import foo.starred.snowbird.utils.safely
import io.github.classgraph.ClassGraph

@Priority
object LevelRendererLoader {
    init {
        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages("foo.starred.athen.api.rendering.level.impl.renderers.impl")
            .scan()
            .use { s ->
                val a = s.getClassesWithAnnotation(LevelRenderer::class.java).loadClasses()

                loop@ for (k in a) {
                    safely {
                        Class.forName(k.name)
                        LevelQueueImpl.renderers.add(k.kotlin.objectInstance as? ILevelRenderer ?: continue@loop)
                    }
                }
            }
    }
}