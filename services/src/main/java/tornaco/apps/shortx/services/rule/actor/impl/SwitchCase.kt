package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
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

        suspend fun executeActions(actionList: List<com.google.protobuf.Any>?) {
            run loop@{
                if (!actionList.isNullOrEmpty()) {
                    actionList.forEach {
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
                        localVars = localVars
                    )

                logger.d("SwitchCase, case, matchCond: $matchCond")

                if (matchCond) {
                    if (case.isBreak) {
                        isBreakCalled = true
                    }
                    logger.i("Execute case branch actions.")
                    updateTheXXTimeToday(shortXContext, case.caseList)
                    executeActions(case.actionList)
                }
            }
        }

        // Default.
        if (!isBreakCalled && d.hasDeft()) {
            run loop@{
                logger.i("Execute default branch actions.")
                val deft = d.deft
                updateTheXXTimeToday(shortXContext, deft.defaultActionList)
                executeActions(deft.defaultActionList)
            }
        }

        logger.i("end.")
    }
}