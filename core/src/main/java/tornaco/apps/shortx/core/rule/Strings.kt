package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.gv.GlobalVar

fun String.compileContextAndVars(cv: RuleContextAndVars): String {
    return with(cv) {
        inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues(globalVarsLazy)
            .replaceFuncParameterInputs(cv.funcParameters)
    }
}

data class RuleContextAndVars(
    val localVars: LocalVars = LocalVars.obtain(),
    val contextData: ContextData = ContextData.obtain(),
    val funcParameters: List<FuncParameterInput> = emptyList(),
    val globalVarsLazy: () -> List<GlobalVar> = { emptyList() },
)

val emptyRuleContextAndVars = RuleContextAndVars()