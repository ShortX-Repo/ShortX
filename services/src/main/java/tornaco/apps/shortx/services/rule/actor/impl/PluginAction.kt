package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.plugin.toParamsData
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.plugin.PluginHelper.loadPluginObjectOrThrow

object PluginAction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PluginAction::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.PluginAction::class.java
        logger.i("execute plugin action: ${d.action}")
        val paramsData = d.params.toParamsData()
        val actionEvaluatorIns =
            requireNotNull(
                loadPluginObjectOrThrow(
                    d.action,
                    androidContext,
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
