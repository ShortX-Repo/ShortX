package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule

object Security {
    private val dangerousMVELKeywords = setOf(
        "reset",
        "uninstall",
    )

    private val dangerousShellKeywords = setOf<String>(
        "rm",
        "mkfs",
        "dd",
    )

    fun checkSecurity(da: DirectAction): Boolean {
        var result = true
        if (!checkActions(da.actionsList)) {
            result = false
        }
        return result
    }

    fun checkSecurity(rule: Rule): Boolean {
        val conditions = rule.conditionsList
        var result = true
        conditions.forEach { proto ->
            if (proto is_ MatchMVEL::class.java) {
                val matchMVEL = proto.unpack(MatchMVEL::class.java)
                val expression = matchMVEL.expression
                if (!checkMVELExpression(expression)) {
                    result = false
                    return@forEach
                }
            }
        }

        if (!checkActions(rule.actionsList)) {
            result = false
        }

        return result
    }

    private fun checkActions(actions: List<ProtoAny>): Boolean {
        var result = true
        actions.forEach { proto ->
            if (proto is_ ExecuteMVEL::class.java) {
                val matchMVEL = proto.unpack(ExecuteMVEL::class.java)
                val expression = matchMVEL.expression
                if (!checkMVELExpression(expression)) {
                    result = false
                    return@forEach
                }
            }

            if (proto is_ ShellCommand::class.java) {
                val shell = proto.unpack(ShellCommand::class.java)
                val command = shell.command
                if (!checkShellCommand(command)) {
                    result = false
                    return@forEach
                }
            }

            if (proto is_ IfThenElse::class.java) {
                val ifThenElse = proto.unpack(IfThenElse::class.java)
                if (!checkActions(ifThenElse.ifActionsList + ifThenElse.elseActionsList)) {
                    result = false
                    return@forEach
                }
            }

            if (proto is_ WhileLoop::class.java) {
                val wl = proto.unpack(WhileLoop::class.java)
                if (!checkActions(wl.actionsList)) {
                    result = false
                    return@forEach
                }
            }
        }

        return result
    }

    private fun checkMVELExpression(expression: String): Boolean {
        val keywordAny = dangerousMVELKeywords.any {
            expression.contains(it)
        }
        return !keywordAny
    }


    private fun checkShellCommand(command: String): Boolean {
        val keywordAny = dangerousShellKeywords.any {
            command.contains(it)
        }
        return !keywordAny
    }

}