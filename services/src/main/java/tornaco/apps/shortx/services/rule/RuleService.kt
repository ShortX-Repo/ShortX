package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.ServiceManager
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class RuleService : SystemService() {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val engine by lazy {
        RuleEngineFactory.newRuleEngine(
            context, handler
        )
    }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        ruleSettingsStore.readBlocking()
        installListeners()
    }

    private fun installListeners() {
        ServiceManager.gesture.onKeyEvent {
            if (it.action == android.view.KeyEvent.ACTION_UP) {
                logger.d("onKeyEvent up for keyCode: ${it.keyCode}")
                val fact = KeyEvent.newBuilder().setKeyCode(it.keyCode).build()
                if (engine.hasAnyRuleMatchFact(getAllRules(), fact)) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    systemServiceScope.launchSafety {
                        engine.fire(
                            fact = KeyEvent.newBuilder().setKeyCode(it.keyCode).build(),
                            rules = getAllRules()
                        )
                    }
                    return@onKeyEvent true
                }
            }
            return@onKeyEvent null
        }

        ServiceManager.gesture.onGestureEvent {
            false
        }
    }

    fun addRule(rule: Rule) {
        ruleSettingsStore.addRule(rule)
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }
}