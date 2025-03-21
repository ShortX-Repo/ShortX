package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
import tornaco.apps.shortx.services.rule.updateTheXXTimeToday


object SwitchCase : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SwitchCase::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SwitchCase::class.java

        val cases = d.caseList

        logger.i("start, ${cases.size} cases, has default: ${d.hasDeft()}")

        var isBreakCalled = false

        suspend fun executeActions(
            actionList: List<com.google.protobuf.Any>?,
            asyncMode: ActionAsyncMode
        ) {
            actionList?.let { runSubActions(it, asyncMode) }
        }

        cases.filter { !it.isDisabled }.forEach { case ->
            if (!isBreakCalled) {
                val matchCond =
                    conditionEvaluator.evaluate(
                        evaluateContext = evaluateContext,
                        condOp = case.caseCondOp,
                        condOpPayload = case.caseCondOpPayload,
                        conditions = case.caseList,
                        contextData = contextData,
                        localVars = localVars,
                        parameterInputs = parameterInputs
                    )

                logger.d("SwitchCase, case, matchCond: $matchCond")

                if (matchCond) {
                    if (case.isBreak) {
                        isBreakCalled = true
                    }
                    logger.i("Execute case branch actions.")
                    updateTheXXTimeToday(shortXContext, case.caseList)
                    executeActions(case.actionList, case.actionAsyncMode)
                }
            }
        }

        // Default.
        if (!isBreakCalled && d.hasDeft()) {
            run loop@{
                logger.i("Execute default branch actions.")
                val deft = d.deft
                updateTheXXTimeToday(shortXContext, deft.defaultActionList)
                executeActions(deft.defaultActionList, ActionAsyncMode.ActionAsyncMode_Sync)
            }
        }

        logger.i("end.")
        Actor.Res.EMPTY
    }
}