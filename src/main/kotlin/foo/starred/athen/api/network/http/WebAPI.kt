package foo.starred.athen.api.network.http

import foo.starred.athen.Athen
import foo.starred.snowbird.api.network.WebUtils

object WebAPI : WebUtils(Athen.modName, Athen.LOGGER)
