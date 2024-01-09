package tornaco.apps.shortx.core.rule

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.gv.GlobalVar

fun String.compileContextAndVars(cv: RuleContextAndVars): String {
    return with(cv) {
        inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues(globalVarsLazy)
    }
}

data class RuleContextAndVars(
    val localVars: LocalVars = LocalVars.obtain(),
    val contextData: ContextData = ContextData.obtain(),
    val globalVarsLazy: () -> List<GlobalVar> = { emptyList() }
)