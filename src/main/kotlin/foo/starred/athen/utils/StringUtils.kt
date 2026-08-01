package foo.starred.athen.utils

fun String.contains(vararg other: String, ignoreCase: Boolean = false): Boolean {
    return other.any { contains(it, ignoreCase) }
}