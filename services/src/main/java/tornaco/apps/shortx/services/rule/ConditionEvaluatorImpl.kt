package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.ConditionEvaluator
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.MVELExecutor

class ConditionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext
) : ConditionEvaluator {
    private val logger = Logger("ConditionEvaluatorImpl")

    override fun evaluate(condition: ProtoAny): Boolean {
        return when {
            condition is_ CurrentPkg::class.java -> {
                val currentPkg = condition unpack_ CurrentPkg::class.java
                val state = shortXContext.appState.currentFgComponentName.value
                currentPkg.pkg.pkgName == state.pkg.pkgName && currentPkg.pkg.userId == state.pkg.userId
            }
            condition is_ True::class.java -> {
                true
            }
            condition is_ False::class.java -> {
                false
            }
            condition is_ MatchMVEL::class.java -> {
                val mvel = condition unpack_ MatchMVEL::class.java
                val res = MVELExecutor(context, shortXContext, mvel.expression).invoke()
                kotlin.runCatching { res as Boolean }.getOrElse {
                    logger.e(it, "MatchMVEL check error")
                    false
                }
            }
            else -> false
        }
    }
}