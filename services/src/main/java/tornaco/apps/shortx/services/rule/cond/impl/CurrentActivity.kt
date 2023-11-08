package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.appComponentToString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object CurrentActivity : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.CurrentActivity::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val currentPkg =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.CurrentActivity::class.java
            val currentResumed = shortXContext.appState.currentResumedActivities()
            val res = currentResumed.any { current ->
                currentPkg.activitiesList.any { comp ->
                    comp == current
                }
            }

            if (currentPkg.isInvert) {
                !res
            } else {
                res
            }.toResult(currentResumed.joinToString { it.appComponentToString() })
        }
    }
}
