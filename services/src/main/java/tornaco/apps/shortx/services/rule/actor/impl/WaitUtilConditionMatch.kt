package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WaitUtilConditionMatch : Actor {
    private const val CHECK_DELAY = 300L

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch::class.java
        val timeout = d.timeout

        val matchCond: suspend () -> Boolean = {
            conditionEvaluator.evaluate(
                evaluateContext = evaluateContext,
                condOp = d.conditionOp,
                condOpPayload = d.condOpPayload,
                conditions = d.conditionList,
                contextData = contextData,
                localVars = localVars
            )
        }

        logger.d("before match condition")
        var checkedCount = 0
        val maxCheckCount =
            if (timeout <= 0) Int.MAX_VALUE else maxOf(1, timeout / CHECK_DELAY).toInt()
        while (!matchCond()) {
            checkedCount++
            onDebugBuild { logger.d("check cond at $checkedCount") }
            require(checkedCount <= maxCheckCount) {
                "Condition not match within time: $timeout(ms)"
            }
            delay(CHECK_DELAY)
        }
        logger.d("after match condition")
    }
}