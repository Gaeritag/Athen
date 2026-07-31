@file:Suppress("UNUSED")

package foo.starred.athen.handlers

import foo.starred.athen.Athen
import foo.starred.snowbird.handlers.data.AbstractScribble

class Scribble(path: String, tts: Int = 15) : AbstractScribble(Athen.modId, path, tts)