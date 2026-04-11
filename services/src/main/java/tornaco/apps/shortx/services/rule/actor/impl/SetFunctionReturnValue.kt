package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.proto.func.FuncReturnValue
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object SetFunctionReturnValue : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetFunctionReturnValue::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res {
        val data = params.action unpack_ tornaco.apps.shortx.core.proto.action.SetFunctionReturnValue::class.java
        val value = data.value.compileContextAndVars(RuleContextAndVars(params))
        params.contextData.override(
            GenericContextData.CONTEXT_FUNC_RETURN_VALUE,
            FuncReturnValue.newBuilder().setValue(value).build()
        )
        return Actor.Res(value)
    }
}