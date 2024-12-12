package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
import tornaco.apps.shortx.services.rule.updateTheXXTimeToday


object IfThenElse : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.IfThenElse::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ IfThenElse::class.java
        logger.d("IfThenElse, check cond with: $conditionEvaluator")
        val matchCond =
            conditionEvaluator.evaluate(
                evaluateContext = evaluateContext,
                condOp = d.ifCondOp,
                condOpPayload = d.ifCondOpPayload,
                conditions = d.ifList,
                contextData = contextData,
                localVars = localVars,
                parameterInputs = parameterInputs
            )
        logger.d("IfThenElse, matchCond: $matchCond")
        if (matchCond) {
            updateTheXXTimeToday(shortXContext, d.ifList)

            if (d.ifActionsList.isNullOrEmpty().not()) {
                runSubActions(d.ifActionsList, d.ifActionAsyncMode)
            }
        } else {
            if (d.elseActionsList.isNullOrEmpty().not()) {
                runSubActions(d.elseActionsList, d.elseActionAsyncMode)
            }
        }
        Actor.Res.EMPTY
    }
}