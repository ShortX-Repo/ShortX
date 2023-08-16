package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.services.ShortXContext

object Interceptors {
    fun checkExecuteMVEL(shortXContext: ShortXContext, androidContext: Context): Boolean {
        require(shortXContext.rule.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_MVEL)) {
            "MVEL engine is disabled, pls enable it in ShortX app Settings."
        }
        require(!androidContext.packageManager.isSafeMode) {
            "Safe mode!"
        }
        return true
    }

    fun checkExecuteShell(shortXContext: ShortXContext, androidContext: Context): Boolean {
        require(shortXContext.rule.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SHELL)) {
            "Shell engine is disabled, pls enable it in ShortX app Settings."
        }
        require(!androidContext.packageManager.isSafeMode) {
            "Safe mode!"
        }
        return true
    }
}