package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object InputText : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InputText::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val ac = params.action unpack_ InputText::class.java
        require(params.context.powerManager.isInteractive) {
            "Can not InputText when device is not interactive"
        }
        params.shortXContext.input.submitText(
            ac.text.inflateContextMap(params.contextData)
                .replaceLocalVarValues(params.localVars)
                .replaceGlobalVarValues {
                    shortXManager.getAllGlobalVars()
                }.apply {
                    logger.p("Input text: $this")
                })
    }
}