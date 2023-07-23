package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteLocalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java
        val localVar = requireNotNull(localVars.getByName(d.varName)) {
            "Local var: ${d.varName} not exists. pls create first."
        }
        val newValue =
            d.valueAsString.inflateContextMap(contextData)
                .replaceLocalVarValues(localVars)
                .replaceGlobalVarValues {
                    shortXContext.rule.getAllGlobalVars()
                }
        localVars.update(localVar.addOrUpdateValue(listOf(newValue)))
    }
}