package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.SystemEnv.inflateSystemEnv
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.RemoteRuleContextAndVars
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.util.processExpressions

fun String.compileContextAndVars(cv: RuleContextAndVars): String {
    return with(cv) {
        processExpressions(
            replaceFuncParameterInputs(cv.funcParameters)
                .inflateContextMap(contextData)
                .replaceLocalVarValues(localVars)
                .replaceGlobalVarValues(globalVarsLazy)
                .inflateSystemEnv()
        )
    }
}

data class RuleContextAndVars(
    val localVars: LocalVars = LocalVars.obtain(),
    val contextData: ContextData = ContextData.obtain(),
    val funcParameters: List<FuncParameterInput> = emptyList(),
    val globalVarsLazy: () -> List<GlobalVar> = { emptyList() },
)

val emptyRuleContextAndVars = RuleContextAndVars()

fun RemoteRuleContextAndVars.toRuleContextAndVars(globalVarsLazy: () -> List<GlobalVar>): RuleContextAndVars {
    return RuleContextAndVars(
        localVars = LocalVars.obtain().apply { addAll(localVarList) },
        contextData = ContextData.obtain()
            .apply { add(contextDataList.associate { it.first to it.second }) },
        funcParameters = funcParameterList,
        globalVarsLazy = globalVarsLazy
    )
}