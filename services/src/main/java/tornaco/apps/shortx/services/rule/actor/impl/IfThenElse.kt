package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object IfThenElse : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.IfThenElse::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ IfThenElse::class.java
        val matchCond =
            conditionEvaluator.evaluate(
                evaluateContext = evaluateContext,
                condOp = d.ifCondOp,
                condOpPayload = d.ifCondOpPayload,
                conditions = d.ifList,
                contextData = contextData
            )
        logger.d("IfThenElse, matchCond: $matchCond")
        if (matchCond) {
            if (d.ifActionsList.isNullOrEmpty().not()) {
                d.ifActionsList.forEach {
                    val evaRes = recursiveExecute(evaluateContext, it, contextData, localVars)
                    if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                        logger.e(evaRes.error, "evaluate fail")
                        if (evaRes.shouldContinue) {
                            logger.d("Action on error is continue execute.")
                        } else {
                            return@forEach
                        }
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                        logger.w("evaluate break!")
                        return@forEach
                    }
                }
            }
        } else {
            if (d.elseActionsList.isNullOrEmpty().not()) {
                d.elseActionsList.forEach {
                    val evaRes =
                        recursiveExecute(evaluateContext, it, contextData, localVars)
                    if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                        logger.e(evaRes.error, "evaluate fail")
                        if (evaRes.shouldContinue) {
                            logger.d("Action on error is continue execute.")
                        } else {
                            return@forEach
                        }
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                        logger.w("evaluate break!")
                        return@forEach
                    }
                }
            }
        }
    }
}