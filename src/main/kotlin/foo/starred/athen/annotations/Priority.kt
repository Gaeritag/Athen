package foo.starred.athen.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Priority(val value: Int = 0)
