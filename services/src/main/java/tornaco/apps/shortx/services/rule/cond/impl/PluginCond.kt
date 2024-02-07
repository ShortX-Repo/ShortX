package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import shortx.plugin.Intents
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.PluginAction
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.plugin.PluginHelper
import tornaco.apps.shortx.services.rule.plugin.toParamsData

object PluginCond : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.PluginCondition::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val d =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.PluginCondition::class.java
            PluginAction.logger.i("execute plugin condition: ${d.pluginId}")
            val paramsData = d.params.toParamsData()
            val pluginId = d.pluginId
            val pluginCondIns =
                requireNotNull(
                    PluginHelper.loadPluginObjectOrThrow(
                        pluginId,
                        context,
                        Intents.META_DATA_ACTION_EVALUATOR_ENTRY
                    )
                ) {
                    "Can not load plugin action evaluator object."
                }

            val actionEvaluator = pluginCondIns as shortx.plugin.ConditionEvaluator
            val condResult = actionEvaluator.evaluate(
                shortx.plugin.ConditionEvaluator.Input(
                    context,
                    emptyMap(),
                    paramsData
                )
            )
            val isMatch = condResult.match
            val reason = condResult.reason

            if (d.isInvert) {
                !isMatch
            } else {
                isMatch
            }.toResult(reason)
        }
    }
}