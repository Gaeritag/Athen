package foo.starred.athen.api.network.http

import foo.starred.athen.Athen
import foo.starred.kontext.api.network.WebUtils

object WebAPI : WebUtils(Athen.Data.name, Athen.LOGGER)
