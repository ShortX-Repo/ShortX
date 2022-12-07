package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope


class RuleService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val engine by lazy {
        RuleEngineFactory.newRuleEngine(
            shortXContext = shortXContext, context = context, handler = handler
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
        shortXContext.gesture.onKeyEvent {
            logger.d("onKeyEvent up for keyCode: ${it.keyCode}")
            val fact = KeyEvent.newBuilder().setKeyCode(it.keyCode).build()
            val enabledRules = getAllRules().filter { rule -> rule.isEnabled }
            if (engine.shouldFire(enabledRules, fact)) {
                if (it.action == android.view.KeyEvent.ACTION_UP) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    systemServiceScope.launchSafety {
                        engine.fire(
                            fact = fact,
                            rules = enabledRules
                        )
                    }
                }
                // False means consume.
                return@onKeyEvent false
            }
            return@onKeyEvent null
        }

        shortXContext.gesture.onGestureEvent {
            logger.d("onGestureEvent: $it")
            val fact = EdgeGesture.newBuilder().setEdge(it.first).setGesture(it.second).build()
            val enabledRules = getAllRules().filter { rule -> rule.isEnabled }
            if (engine.shouldFire(enabledRules, fact)) {
                logger.d("hasAnyRuleMatchFact for onGestureEvent: $it")
                systemServiceScope.launchSafety {
                    engine.fire(
                        fact = fact,
                        rules = enabledRules
                    )
                }
                return@onGestureEvent true
            }
            false
        }
    }

    fun addRule(rule: Rule) {
        ruleSettingsStore.addRule(rule)
    }

    fun deleteRule(ruleId: String) {
        ruleSettingsStore.deleteRule(ruleId)
    }

    fun getRuleById(id: String): Rule? {
        return ruleSettingsStore.getRuleById(id)
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        ruleSettingsStore.setRuleEnabled(ruleId, enable)
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }
}