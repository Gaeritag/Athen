package foo.starred.athen.api.storage

import foo.starred.athen.Athen
import foo.starred.snowbird.api.storage.AbstractJsonStore

class JsonStore(path: String, tts: Int = 15) : AbstractJsonStore(Athen.modId, path, tts)
