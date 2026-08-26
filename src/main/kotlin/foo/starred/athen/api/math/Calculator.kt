package foo.starred.athen.api.math

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.messaging.enums.MessagePrefixType
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.modules.impl.ModSettings
import foo.starred.athen.utils.command
import foo.starred.kommand.IKommand
import foo.starred.kommand.scopes.KommandCommandScope
import foo.starred.snowbird.utils.formatted
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.math.pow

@Load
object Calculator : IKommand<FabricClientCommandSource> {
    override val loader: KommandCommandScope<FabricClientCommandSource> = KommandCommandScope()

    private val tokenRegex = Regex("""\d+(\.\d+)?|[+\-*/x^()]""")
    private val priority = mapOf("+" to 1, "-" to 1, "*" to 2, "x" to 2, "/" to 2, "^" to 3)

    init {
        command {
            "calc" / greedyString("operation") {
                val string = string("operation")
                val result = calc(string).formatted()
                "<gray>$string = <green>$result".mod(MessagePrefixType.SUCCESS)
            }
        }

        run {
            if (!ModSettings.calculator) return@run

            command("calc") {
                greedyString("operation") {
                    val string = string("operation")
                    val result = calc(string).formatted()
                    "<gray>$string = <green>$result".mod(MessagePrefixType.SUCCESS)
                }
            }
        }
    }

    fun calc(str: String): Double {
        val str = str.tokenize()

        val out = mutableListOf<String>()
        val ops = mutableListOf<String>()

        for (s in str) {
            when {
                s.toDoubleOrNull() != null -> out += s

                s in priority -> {
                    while (ops.isNotEmpty() && ops.last() != "(") {
                        val t = priority[ops.last()] ?: break
                        val c = priority[s] ?: break

                        if (t > c || (t == c && s != "^")) out += ops.removeAt(ops.lastIndex)
                        else break
                    }

                    ops += s
                }

                s == "(" -> ops += s

                s == ")" -> {
                    while (ops.isNotEmpty() && ops.last() != "(") out.add(ops.removeAt(ops.lastIndex))

                    if (ops.isNotEmpty() && ops.last() == "(") ops.removeAt(ops.lastIndex)
                }
            }
        }

        while (ops.isNotEmpty()) out.add(ops.removeAt(ops.lastIndex))

        val stack = mutableListOf<Double>()
        for (o in out) {
            o.toDoubleOrNull()?.let {
                stack.add(it)
                continue
            }

            if (o in priority) {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)

                val res = when (o) {
                    "+" -> a + b
                    "-" -> a - b
                    "*", "x" -> a * b
                    "/" -> a / b
                    "^" -> a.pow(b)
                    else -> 0.0
                }

                stack += res
            }
        }

        return stack.first()
    }

    private fun String.tokenize(): List<String> {
        val str = tokenRegex.findAll(replace(" ", "")).map { it.value }.toMutableList()
        val tokens = mutableListOf<String>()

        for (i in str.indices) {
            val token = str[i]
            if (token == "") continue

            val unary = i == 0 || str[i - 1] in priority.keys || str[i - 1] == "("

            if (token == "+" && unary) continue

            if (token == "-" && unary) {
                val next = str.getOrNull(i + 1)
                if (next == "(") {
                    tokens += "0"
                    tokens += "-"
                    continue
                }

                tokens += "-${next ?: "0"}"
                str[i + 1] = ""
                continue
            }

            tokens += token
        }

        return tokens
    }
}
