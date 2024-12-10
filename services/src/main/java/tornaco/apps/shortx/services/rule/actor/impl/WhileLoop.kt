package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions


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
                        localVars = localVars,
                        parameterInputs = parameterInputs
                    ))
                ) {
                    logger.d("WhileLoop inside: $index")
                    runSubActions(d.actionsList, d.actionAsyncMode)
                    delay(delay)
                    index += 1
                }
            }
            logger.d("WhileLoop complete")
        }
    }
}