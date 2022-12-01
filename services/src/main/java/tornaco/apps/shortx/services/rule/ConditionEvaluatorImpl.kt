package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.ConditionEvaluator
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext

class ConditionEvaluatorImpl(private val shortXContext: ShortXContext) : ConditionEvaluator {
    override fun evaluate(condition: ProtoAny): Boolean {
        return when {
            condition is_ CurrentPkg::class.java -> {
                val currentPkg = condition unpack_ CurrentPkg::class.java
                val state = shortXContext.appState.currentFgComponentName.value
                currentPkg.pkgName == state.pkg.pkgName
            }
            condition is_ True::class.java -> {
                true
            }
            condition is_ False::class.java -> {
                false
            }
            else -> false
        }
    }
}