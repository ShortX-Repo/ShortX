package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.systemServiceScope

class RuleService : SystemService() {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        ruleSettingsStore.readBlocking()
    }

    fun addRule(rule: Rule) {
        ruleSettingsStore.addRule(rule)
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }
}