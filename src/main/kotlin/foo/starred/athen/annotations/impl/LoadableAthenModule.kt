package foo.starred.athen.annotations.impl

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoadableAthenModule(val priority: Int = 0)
