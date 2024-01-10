package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ForEachPkgSet : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ForEachPkgSet::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ForEachPkgSet::class.java
        val pkgSet = shortXContext.pkgSet.getPkgSetByLabel(d.pkgSet)
        requireNotNull(pkgSet) {
            "PkgSet: ${d.pkgSet} not exists."
        }
        val actions = d.actionList
        pkgSet.appPkgsList.forEach { appPkg ->
            val app = shortXContext.appState.pkgToApp(appPkg)
            contextData.override(
                ContextDataMapping.ForEachPkgSet.loopAppPkgName.name,
                appPkg.pkgName
            )
            contextData.override(
                ContextDataMapping.ForEachPkgSet.loopAppLabel.name,
                app?.label ?: appPkg.pkgName
            )
            contextData.override(ContextDataMapping.ForEachPkgSet.loopAppUserId.name, appPkg.userId)
            run loop@{
                actions.forEach {
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