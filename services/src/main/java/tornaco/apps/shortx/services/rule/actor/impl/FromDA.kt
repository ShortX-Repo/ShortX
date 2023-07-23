package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.FromDA
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FromDA : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FromDA::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ FromDA::class.java
        val da = requireNotNull(shortXContext.rule.getDirectActionById(d.daId)) {
            "Direct action not found: ${d.daId}"
        }

        if (da.actionsList.isNullOrEmpty().not()) {
            da.actionsList.forEach {
                val evaRes = recursiveExecute("FromDA", it, contextData, localVars)
                if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                    logger.e(evaRes.error, "evaluate fail")
                    if (evaRes.shouldContinue) {
                        logger.d("Action on error is continue execute.")
                    } else {
                        return@forEach
                    }
                } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                    logger.w("evaluate break!")
                    return@forEach
                }
            }
        }

    }
}