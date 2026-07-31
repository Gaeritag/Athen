package foo.starred.athen.api.rendering.level.impl.queue.data

import foo.starred.athen.api.rendering.level.impl.data.base.ILevelExtractable

class ExtractedQueue<T : ILevelExtractable> {
    val depth = mutableListOf<T>()
    val depthless = mutableListOf<T>()

    fun add(entry: T, depth: Boolean) {
        (if (depth) this.depth else depthless).add(entry)
    }

    fun clear() {
        depth.clear()
        depthless.clear()
    }

    fun bool() {
        depth.isEmpty() && depthless.isEmpty()
    }
}