package foo.starred.athen.config.data.base

interface IConfigElementData {
    val name: String
    val key: String
    val parent: String?
    val description: String?

    fun with(key: String, parent: String?, description: String?): IConfigElementData
}