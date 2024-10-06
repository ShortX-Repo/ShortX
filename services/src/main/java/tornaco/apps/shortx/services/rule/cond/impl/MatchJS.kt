package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.MatchJS
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteJS
import tornaco.apps.shortx.services.rule.cond.Cond

object MatchJS : Cond {
    override fun condClass(): Class<out Message> = MatchJS::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            require(Interceptors.checkExecuteMVEL(param.shortXContext, param.context))

            val js = condition unpack_ MatchJS::class.java

            val resString =
                ExecuteJS.executeJS(
                    androidContext = context,
                    shortXContext = shortXContext,
                    contextData = contextData,
                    localVars = localVars,
                    parameterInputs = parameterInputs,
                    rawExpression = js.expression
                )
            val retValue = resString.trim().toBoolean()
            logger.d("resString: $resString retValue: $retValue")

            if (js.isInvert) {
                !retValue
            } else {
                retValue
            }.toResult()
        }
    }
}