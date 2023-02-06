package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.rule.ProtoAny

@DoNotStrip
class ConditionCallable(private val eva: ConditionEvaluatorImpl, private val condition: ProtoAny) {
    fun call(): Boolean {
        return eva.tryEvaluate(condition)
    }
}