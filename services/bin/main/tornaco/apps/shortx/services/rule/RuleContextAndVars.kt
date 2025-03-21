package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.cond.Cond

fun RuleContextAndVars(
    localVars: LocalVars,
    contextData: ContextData,
    shortXContext: ShortXContext
): RuleContextAndVars {
    return RuleContextAndVars(localVars, contextData) {
        shortXContext.rule.getAllGlobalVars()
    }
}

fun RuleContextAndVars(
    handleParams: HandleParams
): RuleContextAndVars {
    return RuleContextAndVars(
        handleParams.localVars,
        handleParams.contextData,
        handleParams.parameterInputs
    ) {
        handleParams.shortXContext.rule.getAllGlobalVars()
    }
}

fun RuleContextAndVars(
    condParam: Cond.CondParam
): RuleContextAndVars {
    return RuleContextAndVars(condParam.localVars, condParam.contextData) {
        condParam.shortXContext.rule.getAllGlobalVars()
    }
}