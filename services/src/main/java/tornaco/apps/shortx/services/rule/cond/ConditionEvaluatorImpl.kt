package tornaco.apps.shortx.services.rule.cond

import android.content.Context
import com.google.protobuf.Descriptors
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor


data class ConditionAttrs(
    val id: String,
    val isDisabled: Boolean,
)


class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val evaluateResultListener: (ConditionEvaluator.EvaluateResult) -> Unit,
) : ConditionEvaluator {
    private val logger = Logger("CondEva")

    // For single
    override suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condition: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars
    ): ConditionEvaluator.EvaluateResult {
        val cond = prebuiltCondFor(condition)
        logger.d("evaluate, cond for ${condition.typeUrl}: $cond")
        if (cond == null) {
            return ConditionEvaluator.EvaluateResult.Break(evaluateContext)
        }
        return kotlin.runCatching {
            // Do not worry about unpack cost, protobuf has cache.
            val unPacked = condition unpack_ cond.condClass()
            val attrs = extractConditionAttrs(unPacked)
            val conditionId = attrs?.id.fallbackOnEmpty("Unknown-Condition-Id")
            logger.d("Condition attrs: $attrs")

            if (attrs != null && attrs.isDisabled) {
                return ConditionEvaluator.EvaluateResult.Disabled(evaluateContext)
            }

            val matchRes =
                cond.evaluate(
                    Cond.CondParam(
                        context,
                        shortXContext,
                        evaluateContext,
                        condition,
                        contextData,
                        localVars
                    )
                )
            ConditionEvaluator.EvaluateResult.Success(
                condition = condition,
                match = matchRes.match,
                reason = matchRes.reason ?: "",
                evaluateContext = evaluateContext
            )
        }.getOrElse {
            ConditionEvaluator.EvaluateResult.Fail(condition, it, evaluateContext)
        }.apply {
            evaluateResultListener(this)
        }
    }

    // For List
    override suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData,
        localVars: LocalVars
    ): Boolean {
        logger.d("evaluate, conditions: ${conditions.size}")
        val ecs = conditions.filter { condition ->
            val cond = prebuiltCondFor(condition)
            if (cond == null) {
                logger.e("Can not find prebuilt cond for ${condition.typeUrl}")
                return@filter false
            }
            val unPacked = condition unpack_ cond.condClass()
            val attrs = extractConditionAttrs(unPacked)
            if (attrs == null) {
                logger.e("Can not find extract attr for ${condition.typeUrl}")
                return@filter false
            }
            return@filter !attrs.isDisabled
        }

        logger.d("evaluate condition: ${evaluateContext.srcTitle} $condOp enabled condition size: ${ecs.size}, context: $contextData")
        return when (condOp) {
            ConditionOperator.ALL -> {
                ecs.all { evaluate(evaluateContext, it, contextData, localVars).isMatch() }
            }

            ConditionOperator.ANY -> {
                ecs.any { evaluate(evaluateContext, it, contextData, localVars).isMatch() }
            }

            ConditionOperator.NONE -> {
                ecs.none { evaluate(evaluateContext, it, contextData, localVars).isMatch() }
            }

            ConditionOperator.MVEL -> {
                val expression = condOpPayload.expression
                if (expression.isNullOrBlank()) return false

                val varMap = mutableMapOf<String, Any>()
                var expressionTranslation = expression
                repeat(ecs.size) { index ->
                    val codeName = "c${index + 1}"
                    varMap[codeName] = ConditionCallable(
                        evaluateContext = evaluateContext,
                        eva = this,
                        condition = ecs[index],
                        contextData = contextData,
                        localVars = localVars
                    )
                    expressionTranslation =
                        expressionTranslation.replace(codeName, "$codeName.call()")
                    logger.d("replace codeName: $codeName, after replace: $expressionTranslation")
                }

                runCatching {
                    val res =
                        MVELExecutor(context, shortXContext, expressionTranslation).invoke(
                            varMap,
                            null
                        )
                    logger.d("evaluate rule condition MVEL: $expressionTranslation, result: $res")
                    val matchRes = res.toString().toBooleanStrictOrNull() ?: false
                    evaluateResultListener(
                        ConditionEvaluator.EvaluateResult.Success(
                            condition = MatchMVEL.newBuilder()
                                .setId("ConditionOperator.MVEL${System.currentTimeMillis()}")
                                .setExpression(expressionTranslation).build().pack_(),
                            match = matchRes,
                            reason = "",
                            evaluateContext = evaluateContext
                        )
                    )
                    matchRes
                }.getOrElse {
                    logger.e(it, "Match MVEL error")
                    evaluateResultListener(
                        ConditionEvaluator.EvaluateResult.Fail(
                            condition = MatchMVEL.newBuilder()
                                .setId("ConditionOperator.MVEL${System.currentTimeMillis()}")
                                .setExpression(expressionTranslation).build().pack_(),
                            error = it,
                            evaluateContext = evaluateContext
                        )
                    )
                    false
                }
            }

            else -> {
                ecs.all { evaluate(evaluateContext, it, contextData, localVars).isMatch() }
            }
        }
    }

    private fun extractConditionAttrs(condition: ProtoMessage): ConditionAttrs? {
        return runCatching {
            val idDescriptor: Descriptors.FieldDescriptor =
                condition.descriptorForType.findFieldByName("id")
            val id: String = condition.getField(idDescriptor) as String

            val isDisabledDescriptor: Descriptors.FieldDescriptor =
                condition.descriptorForType.findFieldByName("isDisabled")
            val isDisabled: Boolean = condition.getField(isDisabledDescriptor) as Boolean

            ConditionAttrs(id, isDisabled)
        }.getOrElse {
            logger.e(it, "extractConditionAttrs: $condition")
            null
        }
    }
}