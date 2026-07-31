@file:Suppress("Unused")

package foo.starred.athen.handlers

import foo.starred.athen.Athen
import foo.starred.snowbird.utils.WebUtils

object Beacon : WebUtils(Athen.modName, Athen.LOGGER)