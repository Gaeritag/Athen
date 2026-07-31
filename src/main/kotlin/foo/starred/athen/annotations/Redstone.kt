package foo.starred.athen.annotations

/**
 * Marks a [foo.starred.athen.modules.Module] as always enabled, ignoring config and location checks.
 * Modules marked with this annotation will always run regardless of settings.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Redstone