package tornaco.apps.shortx.services.rule.actor.impl.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.ForEach.logger
import tornaco.apps.shortx.services.rule.actor.impl.rethrowOrReturnLoop

suspend inline fun HandleParams.runSubActions(
    actionList: List<ProtoAny>,
    asyncMode: ActionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync,
    crossinline beforeExecute: (ProtoAny) -> Unit = {
        logger.d("runSubActions about to execute action: ${it.typeUrl}")
    }
) {
    logger.logOnDebug { ("runSubActions, asyncMode: $asyncMode") }
    when (asyncMode) {
        ActionAsyncMode.ActionAsyncMode_Async -> {
            runSubActionsAsync(actionList, beforeExecute)
        }

        else -> {
            runSubActionsSync(actionList, beforeExecute)
        }
    }
}


suspend inline fun HandleParams.runSubActionsSync(
    actionList: List<ProtoAny>,
    beforeExecute: (ProtoAny) -> Unit
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


suspend inline fun HandleParams.runSubActionsAsync(
    actionList: List<ProtoAny>,
    crossinline beforeExecute: (ProtoAny) -> Unit
) {
    if (actionList.isEmpty()) return
    coroutineScope {
        actionList.map {
            async {
                logger.logOnDebug { ("runSubActionsAsync, about to execute action: ${it.typeUrl}") }
                beforeExecute(it)

                val evaRes =
                    recursiveExecute(
                        evaluateContext,
                        it,
                        contextData,
                        localVars,
                        parameterInputs
                    )
                if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                    val scope = evaRes.scope
                    logger.w("evaluate break! $scope")
                    scope.rethrowOrReturnLoop {
                        // Noop.
                    }
                }
                logger.logOnDebug { ("runSubActionsAsync, action evaluate res: $evaRes") }
            }
        }.awaitAll()
        logger.logOnDebug { ("runSubActionsAsync, all complete.") }
    }
}