package tornaco.apps.shortx.services.policy

import android.content.Context
import android.view.KeyEvent
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.policy.SingleKeyGestureDetector.SingleKeyRule
import tornaco.apps.shortx.services.rule.RuleService
import tornaco.apps.shortx.services.rule.actor.impl.device
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger


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
    fun hasAny() = keycodes.isNotEmpty()

    private fun installKeyRules() {
        val keyEventRules =
            shortXContext.rule.getAllEnabledRules().filter { rule ->
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

        val longPressKeyCodes = keyEventRules.map { rule ->
            rule.longPressKeyCodeList
        }.flatten().distinct()

        logger.d("enabledKeycodes: ${this.keycodes}")
        logger.d("enabledInterceptModeKeyCodes: ${this.interceptModeKeyCodes}")
        logger.d("longPressKeyCodes: $longPressKeyCodes")

        enabledKeycodes.forEach { keyCode ->
            if (!singleKeyGestureDetector.hasRule(keyCode)) {
                singleKeyGestureDetector.addRule(
                    SingleKeyRuleImpl(
                        keycode = keyCode,
                        ruleService = shortXContext.rule,
                        enableLongPress = longPressKeyCodes.contains(keyCode),
                        interceptMode = interceptModeKeyCodes.contains(keyCode)
                    )
                )
                logger.d("Add SingleKeyRule: $keyCode")
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

    private val Rule.longPressKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (data.gesture == KeyGesture.KeyLongPress).select(data.keyCode, null)
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
    val ruleService: RuleService,
    val enableLongPress: Boolean,
    val interceptMode: Boolean
) : SingleKeyRule(
    keycode,
    if (enableLongPress) SingleKeyGestureDetector.KEY_LONGPRESS else 0
) {
    private val logger = Logger("SingleKeyRuleImpl")

    override fun onPress(downTime: Long) {
        logger.d("singleKeyHandler onPress")

        val fact = AdvancedKeyEvent.newBuilder()
            .setKeyCode(keycode)
            .setGesture(KeyGesture.KeyPress)
            .build()
        val enabledRules = ruleService.getAllEnabledRules()
        val das = ruleService.getAllDirectAction()
        val shouldFire = ruleService.shouldFire(enabledRules, das, fact, false)
        logger.d("shouldFire: $shouldFire")
        if (shouldFire) {
            ruleService.publishFact(
                "onPress-$keycode",
                fact,
                mapOf(
                    ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                    ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 1
                )
            )
        } else if (interceptMode) {
            device.pressReleaseKeycode(
                keycode,
                Device.INJECT_MODE_ASYNC
            )
        }
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

    override fun unHandledDown(event: KeyEvent) {
        super.unHandledDown(event)
        logger.d("unHandledDown: $event")
        if (interceptMode) {
            IMSFunc.injectInputEvent(event)
        }
    }

    override fun getMaxMultiPressCount(): Int {
        return 3
    }
}