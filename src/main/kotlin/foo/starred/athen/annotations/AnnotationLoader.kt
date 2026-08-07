package foo.starred.athen.annotations

import foo.starred.athen.events.GameEvent
import foo.starred.athen.events.core.on
import foo.starred.athen.modules.Module
import foo.starred.snowbird.utils.safely
import io.github.classgraph.ClassGraph

object AnnotationLoader {
    fun load(load: String = "foo.starred.athen") {
        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(load)
            .scan()
            .use { s ->
                val a = s.getClassesWithAnnotation(Priority::class.java.name).loadClasses().sortedBy { it.getAnnotation(Priority::class.java)?.value ?: 0 }
                val b = s.getClassesWithAnnotation(Load::class.java.name).loadClasses()

                loop@ for (k in a) {
                    safely {
                        Class.forName(k.name)
                    }
                }

                loop@ for (k in b) {
                    safely {
                        Class.forName(k.name)
                    }
                }

                val d = s.getSubclasses(Module::class.java.name)

                on<GameEvent.Start> {
                    for (m in d) {
                        safely {
                            (m.loadClass().kotlin.objectInstance as? Module)?.observable
                        }
                    }
                }.once()
            }
    }
}