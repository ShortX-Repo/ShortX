package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ForEach : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ForEach::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.IO) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.ForEach::class.java
            if (d.actionsList.isNullOrEmpty().not()) {
                val cv = RuleContextAndVars(params)
                val delimiters = d.delimiterList.map {
                    it.compileContextAndVars(cv)
                }.toTypedArray()
                val dataArray = d.data.split(*delimiters)
                dataArray.forEachIndexed { index, string ->
                    contextData.add(ContextDataMapping.ForEach.foreachIndex.name, index)
                    contextData.add(ContextDataMapping.ForEach.foreachData.name, string)
                    run loop@{
                        d.actionsList.forEach {
                            logger.d("about to execute action: $index $string -- ${it.typeUrl}")
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
            logger.d("ForEach complete")
        }
    }
}