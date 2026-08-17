package foo.starred.athen.config.dsl.base

@Suppress("UNCHECKED_CAST")
abstract class ElementBuilder<T : ElementBuilder<T>>(
    open val parent: String? = null
) {
    var description: String? = null

    fun description(description: String): T = apply {
        this.description = description
    } as T
}