package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.SystemEnv.inflateSystemEnv
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor
import tornaco.apps.shortx.services.rule.cond.Cond

object MatchMVEL : Cond {
    override fun condClass(): Class<out Message> = MatchMVEL::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            require(Interceptors.checkExecuteMVEL(param.shortXContext, param.context))
            val mvel = condition unpack_ MatchMVEL::class.java
            val res = MVELExecutor(
                context,
                shortXContext,
                mvel.expression
                    .inflateSystemEnv(valueTransformer = {
                        "\"$it\""
                    })
                    .inflateContextMap(param.contextData.map.mapValues { entry ->
                        // Wrap all data with ""
                        entry.value?.let {
                            if (it is String) "\"$it\"" else it
                        }
                    }).apply {
                        logger.d("inflated expression: $this")
                    }).invoke(
                vars = contextData.map,
                localVars = localVars,
                parameterInputs = parameterInputs
            )
            val retValue = res as Boolean
            if (mvel.isInvert) {
                !retValue
            } else {
                retValue
            }.toResult()
        }
    }
}