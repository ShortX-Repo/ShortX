package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.cond.prebuiltCondFor

class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val evaluateResultListener: (ConditionEvaluator.EvaluateResult) -> Unit,
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    // For single
    override suspend fun evaluate(
        condition: ProtoAny,
        contextData: ContextData
    ): ConditionEvaluator.EvaluateResult {
        val cond = prebuiltCondFor(condition)
        logger.d("evaluate, cond for ${condition.typeUrl}: $cond")
        if (cond == null) {
            return ConditionEvaluator.EvaluateResult.Break
        }
        return kotlin.runCatching {
            ConditionEvaluator.EvaluateResult.Success(
                condition,
                cond.evaluate(Cond.CondParam(context, shortXContext, condition, contextData))
            )
        }.getOrElse {
            ConditionEvaluator.EvaluateResult.Fail(condition, it)
        }.apply {
            evaluateResultListener(this)
        }
    }

    // For List
    override suspend fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData
    ): Boolean {
        logger.d("evaluate rule condition: $logTag $condOp condition size: ${conditions.size}, context: $contextData")
        return when (condOp) {
            ConditionOperator.ALL -> {
                conditions.all { evaluate(it, contextData).isMatch() }
            }

            ConditionOperator.ANY -> {
                conditions.any { evaluate(it, contextData).isMatch() }
            }

            ConditionOperator.NONE -> {
                conditions.none { evaluate(it, contextData).isMatch() }
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

                val matchMVEL = evaluate(
                    MatchMVEL.newBuilder()
                        .setId("ConditionOperator.MVEL${System.currentTimeMillis()}")
                        .setExpression(expressionTranslation).build().pack_(),
                    contextData
                )
                logger.d("evaluate rule condition MVEL: $expressionTranslation, result: $matchMVEL")
                return matchMVEL.isMatch()
            }

            else -> {
                conditions.all { evaluate(it, contextData).isMatch() }
            }
        }
    }
}