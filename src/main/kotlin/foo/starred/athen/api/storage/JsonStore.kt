package foo.starred.athen.api.storage

import foo.starred.athen.Athen
import foo.starred.kontext.api.storage.AbstractJsonStore

class JsonStore(path: String, tts: Int = 15) : AbstractJsonStore(Athen.Data.id, path, tts)
