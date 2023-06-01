package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.cond.prebuiltCondFor

class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    private fun evaluate(condition: ProtoAny, contextData: ContextData): Boolean {
        val cond = prebuiltCondFor(condition)
        logger.d("evaluate, cond for ${condition.typeUrl}: $cond")
        return cond?.evaluate(Cond.CondParam(context, shortXContext, condition, contextData))
            ?: false
    }

    fun tryEvaluate(condition: ProtoAny, contextData: ContextData): Boolean {
        return kotlin.runCatching { evaluate(condition, contextData) }.getOrElse {
            logger.e(it, "tryEvaluate condition error")
            false
        }
    }

    override fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData
    ): Boolean {
        logger.d("evaluate rule condition: $logTag $condOp condition size: ${conditions.size}, context: $contextData")
        return when (condOp) {
            ConditionOperator.ALL -> {
                conditions.all { tryEvaluate(it, contextData) }
            }

            ConditionOperator.ANY -> {
                conditions.any { tryEvaluate(it, contextData) }
            }

            ConditionOperator.NONE -> {
                conditions.none { tryEvaluate(it, contextData) }
            }

            ConditionOperator.MVEL -> {
                val expression = condOpPayload.expression
                if (expression.isNullOrBlank()) return false

                val varMap = mutableMapOf<String, Any>()
                var expressionTranslation = expression
                repeat(conditions.size) { index ->
                    val codeName = "c${index + 1}"
                    varMap[codeName] = ConditionCallable(
                        eva = this,
                        condition = conditions[index],
                        contextData = contextData
                    )
                    expressionTranslation =
                        expressionTranslation.replace(codeName, "$codeName.call()")
                    logger.d("replace codeName: $codeName, after replace: $expressionTranslation")
                }
                val res = MVELExecutor(context, shortXContext, expressionTranslation).invoke(varMap)
                logger.d("evaluate rule condition MVEL: $expressionTranslation, result: $res")
                return res.toString().toBooleanStrictOrNull() ?: false
            }

            else -> {
                conditions.all { tryEvaluate(it, contextData) }
            }
        }
    }
}