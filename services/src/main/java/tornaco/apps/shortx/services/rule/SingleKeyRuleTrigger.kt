package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger
import tornaco.apps.shortx.services.util.SingleKeyGestureDetector
import tornaco.apps.shortx.services.util.SingleKeyGestureDetector.SingleKeyRule


class SingleKeyRuleTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext,
    private val singleKeyGestureDetector: SingleKeyGestureDetector
) : Trigger {
    private val logger = Logger("SingleKeyRuleTrigger")
    private val keycodes: MutableList<Int> = mutableListOf()
    private val interceptModeKeyCodes: MutableList<Int> = mutableListOf()

    fun hasAnyWithInterceptMode(keyCode: Int) = interceptModeKeyCodes.contains(keyCode)
    fun hasAny(keyCode: Int) = keycodes.contains(keyCode)

    private fun installKeyRules() {
        val keyEventRules =
            shortXContext.rule.getAllRules().filter { it.isEnabled }.filter { rule ->
                rule.factsList.any {
                    it is_ AdvancedKeyEvent::class.java
                }
            }
        logger.d("keyEventRules: ${keyEventRules.map { it.title }}")

        val enabledKeycodes = keyEventRules.map { rule ->
            rule.keyCodeList
        }.flatten().distinct()
        this.keycodes.clear()
        this.keycodes.addAll(enabledKeycodes)

        val enabledInterceptModeKeyCodes = keyEventRules.map { rule ->
            rule.interceptModeKeyCodeList
        }.flatten().distinct()
        this.interceptModeKeyCodes.clear()
        this.interceptModeKeyCodes.addAll(enabledInterceptModeKeyCodes)

        logger.d("enabledKeycodes: ${this.keycodes}")
        logger.d("enabledInterceptModeKeyCodes: ${this.interceptModeKeyCodes}")

        enabledKeycodes.forEach {
            if (!singleKeyGestureDetector.hasRule(it)) {
                singleKeyGestureDetector.addRule(
                    SingleKeyRuleImpl(
                        it,
                        shortXContext.rule
                    )
                )
                logger.d("Add SingleKeyRule: $it")
            }
        }
    }

    private fun checkAndInstallKeyRuleBeforeRuleChange(rule: Rule) {
        val enabledCodes = rule.keyCodeList
        if (enabledCodes.isNotEmpty()) {
            logger.d("ReInstall Key rules since Rule changed: ${rule.title}: $enabledCodes")

            singleKeyGestureDetector.clearRules()
            singleKeyGestureDetector.reset()
            installKeyRules()
        }
    }

    private val Rule.keyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .map { (it unpack_ AdvancedKeyEvent::class.java).keyCode }.distinct()

    private val Rule.interceptModeKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                data.isInterceptMode.select(data.keyCode, null)
            }.distinct()


    override fun register() {
        installKeyRules()
    }

    override fun unRegister() {
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndInstallKeyRuleBeforeRuleChange(rule)
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        checkAndInstallKeyRuleBeforeRuleChange(rule)
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        shortXContext.rule.getRuleById(ruleId)?.let {
            checkAndInstallKeyRuleBeforeRuleChange(it)
        }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        shortXContext.rule.getRuleById(ruleId)?.let {
            checkAndInstallKeyRuleBeforeRuleChange(it)
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        if (oldRule.factsList != newRule.factsList) {
            checkAndInstallKeyRuleBeforeRuleChange(oldRule)
        }
    }
}


private class SingleKeyRuleImpl(
    val keycode: Int,
    val ruleService: RuleService
) : SingleKeyRule(
    keycode,
    SingleKeyGestureDetector.KEY_LONGPRESS or SingleKeyGestureDetector.KEY_VERYLONGPRESS
) {
    private val logger = Logger("SingleKeyRuleImpl")

    override fun onPress(downTime: Long) {
        logger.d("singleKeyHandler onPress")
        ruleService.publishFact(
            "onPress-$keycode",
            AdvancedKeyEvent.newBuilder()
                .setKeyCode(keycode)
                .setGesture(KeyGesture.KeyPress)
                .build(),
            mapOf(
                ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 1
            )
        )
    }

    override fun onLongPress(eventTime: Long) {
        super.onLongPress(eventTime)
        logger.d("singleKeyHandler onLongPress")
        ruleService.publishFact(
            "onLongPress-$keycode",
            AdvancedKeyEvent.newBuilder()
                .setKeyCode(keycode)
                .setGesture(KeyGesture.KeyLongPress)
                .build()
        )
    }

    override fun onVeryLongPress(eventTime: Long) {
        super.onVeryLongPress(eventTime)
        logger.d("singleKeyHandler onVeryLongPress")
    }

    override fun onMultiPress(downTime: Long, count: Int) {
        super.onMultiPress(downTime, count)
        logger.d("singleKeyHandler onMultiPress: $count")

        if (count == 2) {
            ruleService.publishFact(
                "onMultiPress-2-$keycode",
                AdvancedKeyEvent.newBuilder()
                    .setKeyCode(keycode)
                    .setGesture(KeyGesture.KeyMultiplePress)
                    .build(),
                mapOf(
                    ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                    ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 2
                )
            )
        } else if (count == 3) {
            ruleService.publishFact(
                "onMultiPress-3-$keycode",
                AdvancedKeyEvent.newBuilder()
                    .setKeyCode(keycode)
                    .setGesture(KeyGesture.KeyTriplePress)
                    .build(),
                mapOf(
                    ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                    ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 3
                )
            )
        }
    }

    override fun getMaxMultiPressCount(): Int {
        return 3
    }
}