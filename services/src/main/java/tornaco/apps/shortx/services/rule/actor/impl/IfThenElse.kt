package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.updateTheXXTimeToday


object IfThenElse : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.IfThenElse::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ IfThenElse::class.java
        logger.d("IfThenElse, check cond with: $conditionEvaluator")
        val matchCond =
            conditionEvaluator.evaluate(
                evaluateContext = evaluateContext,
                condOp = d.ifCondOp,
                condOpPayload = d.ifCondOpPayload,
                conditions = d.ifList,
                contextData = contextData,
                localVars = localVars
            )
        logger.d("IfThenElse, matchCond: $matchCond")
        if (matchCond) {
            updateTheXXTimeToday(shortXContext, d.ifList)

            run loop@{
                if (d.ifActionsList.isNullOrEmpty().not()) {
                    d.ifActionsList.forEach {
                        val evaRes = recursiveExecute(
                            evaluateContext,
                            it,
                            contextData,
                            localVars,
                            parameterInputs
                        )
                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                            logger.e(evaRes.error, "evaluate fail")
                            if (evaRes.shouldContinue) {
                                logger.d("Action on error is continue execute.")
                            } else {
                                return@loop
                            }
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                            val scope = evaRes.scope
                            logger.w("evaluate break! $scope")
                            scope.rethrowOrReturnLoop {
                                return@loop
                            }
                        }
                    }
                }
            }
        } else {
            run loop@{
                if (d.elseActionsList.isNullOrEmpty().not()) {
                    d.elseActionsList.forEach {
                        logger.d("about to execute action: ${it.typeUrl}")
                        val evaRes =
                            recursiveExecute(
                                evaluateContext,
                                it,
                                contextData,
                                localVars,
                                parameterInputs
                            )
                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                            logger.e(evaRes.error, "evaluate fail")
                            if (evaRes.shouldContinue) {
                                logger.d("Action on error is continue execute.")
                            } else {
                                return@loop
                            }
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                            val scope = evaRes.scope
                            logger.w("evaluate break! $scope")
                            scope.rethrowOrReturnLoop {
                                return@loop
                            }
                        }
                    }
                }
            }

        }
    }
}