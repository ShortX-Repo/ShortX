package tornaco.apps.shortx.services.rule.actor.impl.util

import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.ForEach.logger
import tornaco.apps.shortx.services.rule.actor.impl.rethrowOrReturnLoop

suspend inline fun HandleParams.runSubActions(
    actionList: List<ProtoAny>,
    beforeExecute: (ProtoAny) -> Unit = {
        logger.d("runSubActions about to execute action: ${it.typeUrl}")
    }
) {
    if (actionList.isEmpty()) return
    run loop@{
        actionList.forEach {
            beforeExecute(it)
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