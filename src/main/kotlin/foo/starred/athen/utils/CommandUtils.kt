package foo.starred.athen.utils

import foo.starred.athen.Athen
import foo.starred.snowbird.kommand.ICommand
import foo.starred.snowbird.kommand.dsl.BuilderScope

fun command(block: BuilderScope.() -> Unit) {
    Command.command(Athen.modId, block)
}

private object Command : ICommand