package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WhileLoop : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WhileLoop::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.IO) {
            val d = action unpack_ WhileLoop::class.java
            if (d.actionsList.isNullOrEmpty().not()) {
                val delay = maxOf(10L, d.delay.toLong())
                val repeat = if (d.repeatTimes <= 0) Int.MAX_VALUE else d.repeatTimes
                logger.d("WhileLoop execute, repeat: $repeat")
                var index = 0
                while (
                    isActive &&
                    index < repeat &&
                    (d.conditionsList.isNullOrEmpty() || conditionEvaluator.evaluate(
                        evaluateContext = evaluateContext,
                        condOp = d.condOp,
                        condOpPayload = d.condOpPayload,
                        conditions = d.conditionsList,
                        contextData = contextData,
                        localVars = localVars
                    ))
                ) {
                    logger.d("WhileLoop inside: $index")

                    run loop@ {
                        d.actionsList.forEach {
                            logger.d("about to execute action: ${it.typeUrl}")
                            val evaRes =
                                recursiveExecute(evaluateContext, it, contextData, localVars)
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


                    delay(delay)
                    index += 1
                }
            }
            logger.d("WhileLoop complete")
        }
    }
}