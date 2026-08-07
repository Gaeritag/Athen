package foo.starred.athen.api.rendering.level.impl.queue.base

import foo.starred.athen.api.rendering.level.impl.data.impl.*
import foo.starred.athen.api.rendering.level.impl.queue.data.ExtractedQueue

interface ILevelQueue {
    val beams: MutableList<ExtractedBeam>
    val texts: MutableList<ExtractedText>
    val lines: ExtractedQueue<ExtractedLine>
    val boxes0: ExtractedQueue<ExtractedBox>
    val boxes1: ExtractedQueue<ExtractedBox>
    val circles0: ExtractedQueue<ExtractedCircle>
    val circles1: ExtractedQueue<ExtractedCircle>

    fun clear()
}