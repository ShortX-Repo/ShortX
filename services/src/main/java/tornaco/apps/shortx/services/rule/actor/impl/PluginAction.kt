package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import shortx.plugin.Intents
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.plugin.PluginHelper.loadPluginObjectOrThrow
import tornaco.apps.shortx.services.rule.plugin.toParamsData

object PluginAction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PluginAction::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.PluginAction::class.java
        logger.i("execute plugin action: ${d.pluginId}")
        val paramsData = d.params.toParamsData()
        val pluginId = d.pluginId
        val actionEvaluatorIns =
            requireNotNull(
                loadPluginObjectOrThrow(
                    pluginId,
                    androidContext,
                    Intents.META_DATA_ACTION_EVALUATOR_ENTRY
                )
            ) {
                "Can not load plugin action evaluator object."
            }
        val actionEvaluator = actionEvaluatorIns as shortx.plugin.ActionEvaluator
        actionEvaluator.evaluate(
            shortx.plugin.ActionEvaluator.Input(
                androidContext,
                emptyMap(),
                paramsData
            )
        )
    }
}
