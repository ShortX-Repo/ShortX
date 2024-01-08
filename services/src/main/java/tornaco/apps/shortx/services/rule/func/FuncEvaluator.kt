package tornaco.apps.shortx.services.rule.func

import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.func.Func

interface FuncEvaluator {
    fun evaluate(func: Func, params: List<StringPair>)
}