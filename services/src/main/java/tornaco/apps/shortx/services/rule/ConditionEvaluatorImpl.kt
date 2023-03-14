package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.condition.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.MVELExecutor

class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    private fun evaluate(condition: ProtoAny): Boolean {
        return when {
            condition is_ CurrentPkgList::class.java -> {
                val currentPkg = condition unpack_ CurrentPkgList::class.java
                val state = shortXContext.appState.currentFgComponentName.value
                val requiredPkgList = if (currentPkg.pkgSetsList.isNotEmpty()) {
                    currentPkg.pkgSetsList.flatMap {
                        shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
                    }
                } else {
                    currentPkg.pkgsList
                }
                requiredPkgList.any {
                    it.pkgName == state.pkg.pkgName && it.userId == state.pkg.userId
                }
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

    override fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>
    ): Boolean {
        logger.d("evaluate rule condition: $logTag $condOp condition size: ${conditions.size}")
        return when (condOp) {
            ConditionOperator.ALL -> {
                conditions.all { tryEvaluate(it) }
            }
            ConditionOperator.ANY -> {
                conditions.any { tryEvaluate(it) }
            }
            ConditionOperator.NONE -> {
                conditions.none { tryEvaluate(it) }
            }
            ConditionOperator.MVEL -> {
                val expression = condOpPayload.expression
                if (expression.isNullOrBlank()) return false

                val varMap = mutableMapOf<String, Any>()
                var expressionTranslation = expression
                repeat(conditions.size) { index ->
                    val codeName = "c${index + 1}"
                    varMap[codeName] = ConditionCallable(this, conditions[index])
                    expressionTranslation =
                        expressionTranslation.replace(codeName, "$codeName.call()")
                    logger.d("replace codeName: $codeName, after replace: $expressionTranslation")
                }
                val res = MVELExecutor(context, shortXContext, expressionTranslation).invoke(varMap)
                logger.d("evaluate rule condition MVEL: $expressionTranslation, result: $res")
                return res.toString().toBooleanStrictOrNull() ?: false
            }
            else -> {
                conditions.all { tryEvaluate(it) }
            }
        }
    }
}