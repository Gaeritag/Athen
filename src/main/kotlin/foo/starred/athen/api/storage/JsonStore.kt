package foo.starred.athen.api.storage

import foo.starred.athen.Athen
import foo.starred.snowbird.handlers.data.AbstractScribble

class JsonStore(path: String, tts: Int = 15) : AbstractScribble(Athen.modId, path, tts)