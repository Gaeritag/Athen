package foo.starred.athen.annotations.loader

import foo.starred.athen.annotations.impl.LoadableAthenModule
import foo.starred.klassgraph.KlassGraph
import foo.starred.kontext.util.safely

object AthenAnnotationLoader {
    private val comparator = compareBy<Class<*>>({ it.getAnnotation(LoadableAthenModule::class.java)?.priority ?: 0 }, { it.name })

    fun load(pack: String = "foo.starred.athen") {
        KlassGraph.scan(AthenAnnotationLoader::class.java, pack).use { result ->
            val list = result.annotated<LoadableAthenModule>().sortedWith(comparator)

            for (klass in list) {
                safely {
                    Class.forName(klass.name)
                }
            }
        }
    }
}
