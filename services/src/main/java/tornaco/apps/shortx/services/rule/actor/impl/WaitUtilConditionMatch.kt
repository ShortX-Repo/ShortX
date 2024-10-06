package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.InternalFactListener
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.fact.isMatch
import java.util.concurrent.atomic.AtomicBoolean


object WaitUtilConditionMatch : Actor {
    private const val CHECK_DELAY = 300L

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        handleInternal(this)
    }

    private suspend fun handleInternal(
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
                localVars = localVars,
                parameterInputs = parameterInputs
            )
        }

        val isQuit = AtomicBoolean(false)
        val factListener = object : InternalFactListener(d.id) {
            override suspend fun onFact(
                fact: ProtoMessage,
                initialContextDataLazy: () -> Map<String, Any>
            ) {
                val factMatch = factMatcher.match(fact, d.quitFactsList)
                logger.d("WaitUtilConditionMatch, fact: $fact match: $factMatch")
                if (factMatch.isMatch) {
                    val condRes = d.quitConditionList.isNotEmpty() || conditionEvaluator.evaluate(
                        evaluateContext = evaluateContext,
                        condOp = d.quitConditionOp,
                        condOpPayload = d.quitCondOpPayload,
                        conditions = d.quitConditionList,
                        contextData = contextData,
                        localVars = localVars,
                        parameterInputs = parameterInputs
                    )
                    logger.d("WaitUtilConditionMatch, condition match: $condRes")
                    if (condRes) {
                        isQuit.set(true)
                    }
                }
            }
        }
        if (d.quitEnabled) {
            shortXContext.rule.registerInternalFactListener(factListener)
        }

        logger.d("before match condition")
        var checkedCount = 0
        val maxCheckCount =
            if (timeout <= 0) Int.MAX_VALUE else maxOf(1, timeout / CHECK_DELAY).toInt()
        while (!matchCond()) {
            checkedCount++
            logger.logOnDebug("check cond at $checkedCount")
            require(checkedCount <= maxCheckCount) {
                shortXContext.rule.unregisterInternalFactListener(factListener)
                "Condition not match within time: $timeout(ms)"
            }
            delay(CHECK_DELAY)

            require(!isQuit.get()) {
                shortXContext.rule.unregisterInternalFactListener(factListener)
                "Quit(退出条件)"
            }
        }
        shortXContext.rule.unregisterInternalFactListener(factListener)
        logger.d("after match condition")
    }
}