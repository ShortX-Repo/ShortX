package tornaco.apps.shortx.services.policy

import android.content.Context
import android.view.KeyEvent
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.common.KeyGesturePhase
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.quitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.policy.SingleKeyGestureDetector.SingleKeyRule
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.rule.RuleService
import tornaco.apps.shortx.services.rule.actor.impl.InjectKeyCode
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger
import java.util.concurrent.ConcurrentHashMap


class SingleKeyRuleTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext,
    private val singleKeyGestureDetector: SingleKeyGestureDetector
) : Trigger {
    private val logger = Logger("SingleKeyRuleTrigger")
    private val keycodes: MutableList<Int> = mutableListOf()
    private val interceptModeKeyCodes: MutableList<Int> = mutableListOf()
    private val keyDownKeyCodes: MutableList<Int> = mutableListOf()
    private val keyUpKeyCodes: MutableList<Int> = mutableListOf()
    private val keyDownUpInterceptModeKeyCodes: MutableList<Int> = mutableListOf()

    // keyCode -> KeyGesture that is pending a GestureUp phase publish on key release
    private val pendingGestureUp = ConcurrentHashMap<Int, KeyGesture>()

    fun hasAnyWithInterceptMode(keyCode: Int) = interceptModeKeyCodes.contains(keyCode)
    fun hasKeyDownUpInterceptMode(keyCode: Int) = keyDownUpInterceptModeKeyCodes.contains(keyCode)
    fun hasAny(keyCode: Int) = keycodes.contains(keyCode)
    fun hasAny() = keycodes.isNotEmpty()

    private fun installKeyRules() {
        val keyEventRules =
            shortXContext.rule.getAllEnabledRules().filter { rule ->
                rule.factsAndQuitFacts.any {
                    it is_ AdvancedKeyEvent::class.java
                }
            }
        logger.d("keyEventRules: ${keyEventRules.map { it.title }}")

        val keyEventDAs =
            shortXContext.rule.getAllDirectAction().filter { da ->
                da.quitFacts.any {
                    it is_ AdvancedKeyEvent::class.java
                }
            }
        logger.d("keyEventDAs: ${keyEventDAs.map { it.title }}")

        val enabledKeycodes = keyEventRules.map { rule ->
            rule.keyCodeList
        }.flatten().distinct() + keyEventDAs.map { da ->
            da.keyCodeList
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
        val doublePressKeyCodes = keyEventRules.map { rule ->
            rule.doublePressKeyCodeList
        }.flatten().distinct()
        val triplePressKeyCodes = keyEventRules.map { rule ->
            rule.triplePressKeyCodeList
        }.flatten().distinct()

        val keyDownCodes = keyEventRules.map { rule ->
            rule.keyDownKeyCodeList
        }.flatten().distinct()
        this.keyDownKeyCodes.clear()
        this.keyDownKeyCodes.addAll(keyDownCodes)

        val keyUpCodes = keyEventRules.map { rule ->
            rule.keyUpKeyCodeList
        }.flatten().distinct()
        this.keyUpKeyCodes.clear()
        this.keyUpKeyCodes.addAll(keyUpCodes)

        val keyDownUpInterceptCodes = keyEventRules.flatMap { rule ->
            rule.factsList.filter { it is_ AdvancedKeyEvent::class.java }
                .mapNotNull {
                    val data = (it unpack_ AdvancedKeyEvent::class.java)
                    if (!data.isDisabled && data.isInterceptMode
                        && (data.gesture == KeyGesture.KeyDown || data.gesture == KeyGesture.KeyUp)
                    ) data.keyCode else null
                }
        }.distinct()
        this.keyDownUpInterceptModeKeyCodes.clear()
        this.keyDownUpInterceptModeKeyCodes.addAll(keyDownUpInterceptCodes)

        logger.d("enabledKeycodes: ${this.keycodes}")
        logger.d("enabledInterceptModeKeyCodes: ${this.interceptModeKeyCodes}")
        logger.d("longPressKeyCodes: $longPressKeyCodes")
        logger.d("doublePressKeyCodes: $doublePressKeyCodes")
        logger.d("triplePressKeyCodes: $triplePressKeyCodes")
        logger.d("keyDownKeyCodes: $keyDownCodes")
        logger.d("keyUpKeyCodes: $keyUpCodes")

        enabledKeycodes.forEach { keyCode ->
            val needsGestureDetection = longPressKeyCodes.contains(keyCode)
                    || doublePressKeyCodes.contains(keyCode)
                    || triplePressKeyCodes.contains(keyCode)
                    || keyEventRules.any { rule ->
                rule.factsList.filter { it is_ AdvancedKeyEvent::class.java }
                    .any {
                        val data = (it unpack_ AdvancedKeyEvent::class.java)
                        !data.isDisabled && data.keyCode == keyCode && data.gesture == KeyGesture.KeyPress
                    }
            }
            if (needsGestureDetection && !singleKeyGestureDetector.hasRule(keyCode)) {
                singleKeyGestureDetector.addRule(
                    SingleKeyRuleImpl(
                        keycode = keyCode,
                        ruleService = shortXContext.rule,
                        enableLongPress = longPressKeyCodes.contains(keyCode),
                        enableDoublePress = doublePressKeyCodes.contains(keyCode),
                        enableTriplePress = triplePressKeyCodes.contains(keyCode),
                        interceptMode = interceptModeKeyCodes.contains(keyCode),
                        pendingGestureUp = pendingGestureUp
                    )
                )
                logger.d("Add SingleKeyRule: $keyCode")
            }
        }
    }

    fun onRawKeyDown(event: KeyEvent) {
        val keyCode = event.keyCode
        if (keyDownKeyCodes.contains(keyCode)) {
            logger.d("onRawKeyDown: $keyCode")
            val fact = AdvancedKeyEvent.newBuilder()
                .setKeyCode(keyCode)
                .setGesture(KeyGesture.KeyDown)
                .build()
            shortXContext.rule.publishFactChecked(AdvancedKeyEvent.getDefaultInstance()) {
                FactPublishData("onKeyDown-$keyCode", fact) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keyCode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 0
                    )
                }
            }
        }
    }

    fun onRawKeyUp(event: KeyEvent) {
        val keyCode = event.keyCode
        if (keyUpKeyCodes.contains(keyCode)) {
            logger.d("onRawKeyUp: $keyCode")
            val fact = AdvancedKeyEvent.newBuilder()
                .setKeyCode(keyCode)
                .setGesture(KeyGesture.KeyUp)
                .build()
            shortXContext.rule.publishFactChecked(AdvancedKeyEvent.getDefaultInstance()) {
                FactPublishData("onKeyUp-$keyCode", fact) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keyCode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 0
                    )
                }
            }
        }

        val pendingGesture = pendingGestureUp.remove(keyCode)
        if (pendingGesture != null) {
            logger.d("onRawKeyUp publishing GestureUp for gesture=$pendingGesture keyCode=$keyCode")
            val fact = AdvancedKeyEvent.newBuilder()
                .setKeyCode(keyCode)
                .setGesture(pendingGesture)
                .setPhase(KeyGesturePhase.GestureUp)
                .build()
            shortXContext.rule.publishFactChecked(AdvancedKeyEvent.getDefaultInstance()) {
                FactPublishData("onGestureUp-${pendingGesture.name}-$keyCode", fact) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keyCode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 0
                    )
                }
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

    private val DirectAction.keyCodeList
        get() = this.quitFacts.filter { it is_ AdvancedKeyEvent::class.java }
            .map { (it unpack_ AdvancedKeyEvent::class.java).keyCode }.distinct()

    private val Rule.interceptModeKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.isInterceptMode).select(data.keyCode, null)
            }.distinct()

    private val Rule.longPressKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.gesture == KeyGesture.KeyLongPress).select(
                    data.keyCode,
                    null
                )
            }.distinct()
    private val Rule.doublePressKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.gesture == KeyGesture.KeyMultiplePress).select(
                    data.keyCode,
                    null
                )
            }.distinct()
    private val Rule.triplePressKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.gesture == KeyGesture.KeyTriplePress).select(
                    data.keyCode,
                    null
                )
            }.distinct()
    private val Rule.keyDownKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.gesture == KeyGesture.KeyDown).select(
                    data.keyCode,
                    null
                )
            }.distinct()
    private val Rule.keyUpKeyCodeList
        get() = this.factsList.filter { it is_ AdvancedKeyEvent::class.java }
            .mapNotNull {
                val data = (it unpack_ AdvancedKeyEvent::class.java)
                (!data.isDisabled && data.gesture == KeyGesture.KeyUp).select(
                    data.keyCode,
                    null
                )
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
    val enableDoublePress: Boolean,
    val enableTriplePress: Boolean,
    val interceptMode: Boolean,
    val pendingGestureUp: ConcurrentHashMap<Int, KeyGesture>
) : SingleKeyRule(
    keycode,
    if (enableLongPress) SingleKeyGestureDetector.KEY_LONGPRESS else 0
) {
    private val logger = Logger("SingleKeyRuleImpl")

    private fun publishGestureFact(
        gesture: KeyGesture,
        phase: KeyGesturePhase,
        reason: String,
        pressTimes: Int
    ) {
        val fact = AdvancedKeyEvent.newBuilder()
            .setKeyCode(keycode)
            .setGesture(gesture)
            .setPhase(phase)
            .build()
        ruleService.publishFactChecked(AdvancedKeyEvent.getDefaultInstance()) {
            FactPublishData(reason, fact) {
                mapOf(
                    ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                    ContextDataMapping.AdvancedKeyEvent.pressTimes.name to pressTimes
                )
            }
        }
    }

    override fun onPress(downTime: Long) {
        logger.d("singleKeyHandler onPress")
        val fact = AdvancedKeyEvent.newBuilder()
            .setKeyCode(keycode)
            .setGesture(KeyGesture.KeyPress)
            .build()
        val enabledRules = ruleService.getAllEnabledRules()
        val das = ruleService.getAllDirectAction()
        val shouldFire = ruleService.shouldFire(fact, false, enabledRules, das)
        logger.d("shouldFire: $shouldFire")
        if (shouldFire) {
            ruleService.publishFactChecked(
                AdvancedKeyEvent.getDefaultInstance()
            ) {
                FactPublishData(
                    reason = "onPress-$keycode",
                    fact = fact
                ) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 1
                    )
                }
            }
        } else if (interceptMode) {
            InjectKeyCode.injectKey(keycode)
        }

        publishGestureFact(KeyGesture.KeyPress, KeyGesturePhase.GestureDown, "onPress-Down-$keycode", 1)
        publishGestureFact(KeyGesture.KeyPress, KeyGesturePhase.GestureUp, "onPress-Up-$keycode", 1)
    }

    override fun onLongPress(eventTime: Long) {
        super.onLongPress(eventTime)
        logger.d("singleKeyHandler onLongPress")
        ruleService.publishFactChecked(AdvancedKeyEvent.getDefaultInstance()) {
            FactPublishData(
                "onLongPress-$keycode",
                AdvancedKeyEvent.newBuilder()
                    .setKeyCode(keycode)
                    .setGesture(KeyGesture.KeyLongPress)
                    .build()
            )
        }

        publishGestureFact(KeyGesture.KeyLongPress, KeyGesturePhase.GestureDown, "onLongPress-Down-$keycode", 1)
        pendingGestureUp[keycode] = KeyGesture.KeyLongPress
    }

    override fun onMultiPress(downTime: Long, count: Int) {
        super.onMultiPress(downTime, count)
        logger.d("singleKeyHandler onMultiPress: $count")

        if (count == 2) {
            ruleService.publishFactChecked(
                AdvancedKeyEvent.getDefaultInstance()
            ) {
                FactPublishData(
                    "onMultiPress-2-$keycode",
                    AdvancedKeyEvent.newBuilder()
                        .setKeyCode(keycode)
                        .setGesture(KeyGesture.KeyMultiplePress)
                        .build()
                ) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 2
                    )
                }
            }
            publishGestureFact(KeyGesture.KeyMultiplePress, KeyGesturePhase.GestureDown, "onMultiPress-2-Down-$keycode", 2)
            publishGestureFact(KeyGesture.KeyMultiplePress, KeyGesturePhase.GestureUp, "onMultiPress-2-Up-$keycode", 2)
        } else if (count == 3) {
            ruleService.publishFactChecked(
                AdvancedKeyEvent.getDefaultInstance()
            ) {
                FactPublishData(
                    "onMultiPress-3-$keycode",
                    AdvancedKeyEvent.newBuilder()
                        .setKeyCode(keycode)
                        .setGesture(KeyGesture.KeyTriplePress)
                        .build(),
                ) {
                    mapOf(
                        ContextDataMapping.AdvancedKeyEvent.keyCode.name to keycode,
                        ContextDataMapping.AdvancedKeyEvent.pressTimes.name to 3
                    )
                }
            }
            publishGestureFact(KeyGesture.KeyTriplePress, KeyGesturePhase.GestureDown, "onMultiPress-3-Down-$keycode", 3)
            publishGestureFact(KeyGesture.KeyTriplePress, KeyGesturePhase.GestureUp, "onMultiPress-3-Up-$keycode", 3)
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
        return if (enableTriplePress) 3
        else if (enableDoublePress) 2
        else 1
    }
}
