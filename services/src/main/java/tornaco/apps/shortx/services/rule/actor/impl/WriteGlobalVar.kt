package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteGlobalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ WriteGlobalVar::class.java
        val globalVar = requireNotNull(shortXContext.rule.getGlobalVarByName(d.varName)) {
            "Global var: ${d.varName} not exists. pls create first."
        }
        val newValue =
            d.valueAsString.inflateContextMap(contextData).replaceGlobalVarValues {
                shortXContext.rule.getAllGlobalVars()
            }
        globalVar.addOrUpdateValue(listOf(newValue))
    }
}