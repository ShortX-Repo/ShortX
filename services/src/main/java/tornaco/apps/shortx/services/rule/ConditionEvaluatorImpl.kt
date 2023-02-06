package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.condition.*
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.ConditionEvaluator
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.MVELExecutor

class ConditionEvaluatorImpl(
    private val context: Context, private val shortXContext: ShortXContext
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    private fun evaluate(condition: ProtoAny): Boolean {
        return when {
            condition is_ CurrentPkg::class.java -> {
                val currentPkg = condition unpack_ CurrentPkg::class.java
                val state = shortXContext.appState.currentFgComponentName.value
                currentPkg.pkg.pkgName == state.pkg.pkgName && currentPkg.pkg.userId == state.pkg.userId
            }
            condition is_ True::class.java -> {
                true
            }
            condition is_ False::class.java -> {
                false
            }
            condition is_ MatchMVEL::class.java -> {
                val mvel = condition unpack_ MatchMVEL::class.java
                val res = MVELExecutor(context, shortXContext, mvel.expression).invoke()
                kotlin.runCatching { res as Boolean }.getOrElse {
                    logger.e(it, "MatchMVEL check error")
                    false
                }
            }
            else -> false
        }
    }

    fun tryEvaluate(condition: ProtoAny): Boolean {
        return kotlin.runCatching { evaluate(condition) }.getOrElse {
            logger.e(it, "tryEvaluate condition error")
            false
        }
    }

    override fun evaluate(rule: Rule, condition: List<ProtoAny>): Boolean {
        logger.d("evaluate rule condition: ${rule.title} ${rule.condOp}")
        return when (rule.condOp) {
            ConditionOperator.ALL -> {
                condition.all { tryEvaluate(it) }
            }
            ConditionOperator.ANY -> {
                condition.any { tryEvaluate(it) }
            }
            ConditionOperator.NONE -> {
                condition.none { tryEvaluate(it) }
            }
            ConditionOperator.MVEL -> {
                val expression = rule.condOpPayload.expression
                if (expression.isNullOrBlank()) return false

                val varMap = mutableMapOf<String, Any>()
                var expressionTranslation = expression
                repeat(condition.size) { index ->
                    val codeName = "c$index"
                    varMap[codeName] = ConditionCallable(this, condition[index])
                    expressionTranslation =
                        expressionTranslation.replace(codeName, "$codeName.call()")
                }

                val res = MVELExecutor(context, shortXContext, expressionTranslation).invoke(varMap)
                return res.toString().toBooleanStrictOrNull() ?: false
            }
            else -> {
                condition.all { tryEvaluate(it) }
            }
        }
    }
}