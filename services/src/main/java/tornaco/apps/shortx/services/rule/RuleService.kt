package tornaco.apps.shortx.services.rule

import android.annotation.TestApi
import android.content.Context
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.RemoteCallbackList
import android.util.Log
import android.view.KeyEvent.KEYCODE_POWER
import android.view.KeyEvent.KEYCODE_STEM_PRIMARY
import android.view.KeyEvent.KEYCODE_VOLUME_DOWN
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.os.CancellationSignal
import tornaco.apps.shortx.core.os.ICancellationSignal
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionList
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.getTypeUrl
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleList
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.proto.toggles.ToggleStateHandleByCondition
import tornaco.apps.shortx.core.proto.toggles.ToggleStateHandleByStore
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.IToggleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.DirectActionSetsStore
import tornaco.apps.shortx.services.data.DirectActionSettingsStore
import tornaco.apps.shortx.services.data.GlobalVarStore
import tornaco.apps.shortx.services.data.RuleSetsStore
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.ToggleSettingsStore
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.policy.KeyCombinationManager
import tornaco.apps.shortx.services.policy.KeyCombinationManager.TwoKeysCombinationRule
import tornaco.apps.shortx.services.policy.KeyCombinationTrigger
import tornaco.apps.shortx.services.policy.SingleKeyGestureDetector
import tornaco.apps.shortx.services.policy.SingleKeyRuleTrigger
import tornaco.apps.shortx.services.rule.actor.ActionEvaluateRecorder
import tornaco.apps.shortx.services.rule.actor.PrebuiltActor
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteMVEL
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequestLogging
import tornaco.apps.shortx.services.rule.actor.impl.ShellCommand
import tornaco.apps.shortx.services.rule.actor.toActionEvaluateRecord
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluateRecorder
import tornaco.apps.shortx.services.rule.cond.PrebuiltCond
import tornaco.apps.shortx.services.rule.cond.toConditionEvaluateRecord
import tornaco.apps.shortx.services.rule.fact.FactPublishRecorder
import tornaco.apps.shortx.services.rule.fact.toFactPublishRecord
import tornaco.apps.shortx.services.rule.fact.trigger.KeyPressTimesInIntervalListener
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.callIfAlive
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class RuleService(shortXContext: ShortXContext) : SystemService(shortXContext, "RuleService") {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val dasSettingsStore: DirectActionSettingsStore by lazy {
        DirectActionSettingsStore(systemServiceScope)
    }

    private val toggleSettingsStore by lazy {
        ToggleSettingsStore(systemServiceScope)
    }

    private val ruleSetsStore by lazy {
        RuleSetsStore(systemServiceScope)
    }

    private val daSetsStore by lazy {
        DirectActionSetsStore(systemServiceScope)
    }

    private val globalVarStore: GlobalVarStore by lazy {
        GlobalVarStore(
            scope = systemServiceScope,
            encKey = requireNotNull(store.read(Settings.StringSettings.ServiceIns)) {
                "ServiceIns is null"
            },
            onAddOrUpdate = { gv ->
                globalVarObs.broadcast {
                    kotlin.runCatching { it.onAddOrUpdate(gv.name) }
                        .onFailure { logger.e(it, "Call onAddOrUpdate") }
                }
            },
            onDelete = { gv ->
                globalVarObs.broadcast {
                    kotlin.runCatching { it.onDelete(gv.name) }
                        .onFailure { logger.e(it, "Call onDelete") }
                }
            })
    }

    private val singleKeyGestureDetector: SingleKeyGestureDetector by lazy {
        SingleKeyGestureDetector.get().also {
            SingleKeyGestureDetector.sDefaultLongPressTimeout =
                shortXContext.gesture.getKeyDetectorLongPressTimeoutMS().toLong().apply {
                    logger.i("sDefaultLongPressTimeout: $this")
                }
        }
    }
    private val singleKeyRuleTrigger by lazy {
        SingleKeyRuleTrigger(
            context,
            shortXContext,
            singleKeyGestureDetector
        )
    }

    private val keyCombinationTrigger by lazy {
        KeyCombinationTrigger(
            context,
            shortXContext
        )
    }

    private val systemKeyCombineDetector by lazy {
        KeyCombinationManager(handler).apply {
            addRule(object : TwoKeysCombinationRule(KEYCODE_VOLUME_DOWN, KEYCODE_POWER) {
            })
            addRule(object : TwoKeysCombinationRule(KEYCODE_POWER, KEYCODE_STEM_PRIMARY) {
            })
        }
    }

    private val allTriggers by lazy {
        allTriggers(context, shortXContext).apply {
            // These are internal Triggers.
            add(singleKeyRuleTrigger)
            add(keyCombinationTrigger)
        }
    }

    private val jobStatusListeners = RemoteCallbackList<IJobStatusListener>()
    private val actionEvaluateListener = RemoteCallbackList<IActionEvaluateListener>()

    private val globalVarObs = RemoteCallbackList<IGlobalVarObserver>()
    private val ruleObs = RemoteCallbackList<IRuleObserver>()
    private val daObs = RemoteCallbackList<IDAObserver>()
    private val toggleObs = RemoteCallbackList<IToggleObserver>()

    private val actionEvaluateRecorder = ActionEvaluateRecorder()
    private val conditionEvaluateRecorder = ConditionEvaluateRecorder()
    private val factPublishRecorder = FactPublishRecorder()

    // For internal usage, such as Actor listen to a fact to act.
    private val internalFactListeners: MutableList<InternalFactListener> = mutableListOf()

    private val forceDisableShortXListener = object : KeyPressTimesInIntervalListener(12, 10000) {
        override fun keyPressCountHit() {
            if (isForceDisableShortXByKeyPressEnabled()) {
                logger.e("forceDisableShortXListener - keyPressCountHit.")
                forceDisableShortX()
            }
        }
    }

    private val engine by lazy {
        RuleEngineFactory.newRuleEngine(
            shortXContext = shortXContext,
            androidContext = context,
            handler = handler,
            onJobStarted = { id ->
                systemServiceScope.launchSafety {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobStarted(id) }
                            .onFailure { logger.e(it, "call onJobStarted") }
                    }
                }
            },
            onJobCompleted = { id ->
                systemServiceScope.launchSafety {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobCompleted(id) }
                            .onFailure { logger.e(it, "call onJobCompleted") }
                    }
                }
            },
            onJobCancelled = { id ->
                systemServiceScope.launchSafety {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobCancelled(id) }
                            .onFailure { logger.e(it, "call onJobCancelled") }
                    }
                }
            },
            actionEvaluateStartListener = { actionId ->
                logger.d("ActionLifecycle actionEvaluateStartListener: $actionId")
                actionEvaluateRecorder.addEvaluating(actionId)
                systemServiceScope.launchSafety {
                    actionEvaluateListener.broadcast {
                        kotlin.runCatching { it.onStart(actionId) }
                            .onFailure { logger.e(it, "call onStart") }
                    }
                }
            },
            actionEvaluateResultListener = { result ->
                logger.p {
                    "ActionLifecycle actionEvaluateResultListener srcTitle: ${result.action.typeUrl} ${result.evaluateContext.srcTitle}"
                }
                actionEvaluateRecorder.removeEvaluating(result.actionId)
                systemServiceScope.launchSafety {
                    actionEvaluateListener.broadcast {
                        kotlin.runCatching { it.onComplete(result.actionId) }
                            .onFailure { logger.e(it, "call onComplete") }
                    }
                }
                if (isEvaluateRecordEnabled(result.evaluateContext.srcId)) {
                    result.toActionEvaluateRecord()?.let { actionEvaluateRecorder.addResult(it) }
                }
            },
            conditionEvaluateResultListener = { result ->
                logger.d("conditionEvaluateResultListener: $result")
                if (isEvaluateRecordEnabled(result.evaluateContext.srcId)) {
                    conditionEvaluateRecorder.add(result.toConditionEvaluateRecord())
                }
            }
        )
    }

    private val publishInterceptor by lazy {
        FactPublishInterceptor(shortXContext, systemServiceScope)
    }

    override fun start(context: Context) {
        super.start(context)
        // Call this to init the cache.
        publishInterceptor.intercept(SystemReady.getDefaultInstance())
    }

    override fun systemReady() {
        super.systemReady()

        // Install ServiceIns store
        if (store.read(Settings.StringSettings.ServiceIns).isNullOrBlank()) {
            store.write(Settings.StringSettings.ServiceIns, UUID.randomUUID().toString())
            logger.i("Service Ins installed.")
        }

        ruleSettingsStore.readBlocking()
        dasSettingsStore.readBlocking()
        toggleSettingsStore.readBlocking()
        globalVarStore.readBlocking()
        ruleSetsStore.readBlocking()
        daSetsStore.readBlocking()

        installListeners()

        PrebuiltActor.entries.forEach {
            it.actor.init(context, shortXContext)
        }
        logger.d("PrebuiltActor init complete")

        PrebuiltCond.entries.forEach {
            it.cond.init(context, shortXContext)
        }
        logger.d("PrebuiltCond init complete")

        HttpRequestLogging.isLogEnabled = isHttpRequestLogEnabled()

        // Add guide DA
        if (getAllDirectAction().isEmpty()) {
            addDirectAction(
                DirectAction.newBuilder()
                    .setId("UserGuide")
                    .setCreateTime(System.currentTimeMillis())
                    .setTitle(i18NIns["ui.user.guide"])
                    .setDescription("\uD83E\uDD13")
                    .setAuthor(Author.newBuilder().setName("ShortX").build())
                    .addAllActions(
                        listOf(
                            StartActivityUrlSchema.newBuilder()
                                .setId("startUserGuideByUrl")
                                .setUrlSchema(BuildProp.WIKI_URL)
                                .setUserId(myUserId)
                                .build().pack_()
                        )
                    )
                    .build()
            )
        }
    }

    override fun shutdown() {
        super.shutdown()
        allTriggers.forEach {
            kotlin.runCatching {
                logger.d("Un-Register trigger: $it")
                it.unRegister()
            }.onFailure { logger.e("Fail unRegister trigger $it") }
        }
    }

    private fun installListeners() {
        shortXContext.gesture.onKeyEvent {
            logger.d("onKeyEvent up for keyCode: ${it.keyCode}")

            if (it.action == android.view.KeyEvent.ACTION_UP && isKeyEventPromptEnabled()) {
                handler.postSafety {
                    Toast.makeText(context, "Key code: ${it.keyCode}", Toast.LENGTH_SHORT).show()
                }
            }

            if (it.keyCode == android.view.KeyEvent.KEYCODE_POWER) {
                if (it.action == android.view.KeyEvent.ACTION_DOWN) {
                    forceDisableShortXListener.onKeyPressed()
                }
            } else {
                forceDisableShortXListener.onOtherKeyPressed()
            }

            // Check if it accessibility service.
            if ((it.flags and android.view.KeyEvent.FLAG_IS_ACCESSIBILITY_EVENT) != 0) {
                return@onKeyEvent null
            }

            if ((it.flags and android.view.KeyEvent.FLAG_FALLBACK) == 0) {
                systemKeyCombineDetector.interceptKey(it, true)
                if (systemKeyCombineDetector.isKeyConsumed(it)) {
                    // Check if system has handle combine key.
                    singleKeyGestureDetector.reset()
                    return@onKeyEvent null
                }
            }

            if (BuildPropInternal.APP_IS_DEBUG) {
                if ((it.flags and android.view.KeyEvent.FLAG_FALLBACK) == 0) {
                    keyCombinationTrigger.interceptKey(it)
                    if (keyCombinationTrigger.isKeyConsumed(it)) {
                        singleKeyGestureDetector.reset()
                        return@onKeyEvent false
                    }
                }
            }

            // Check ADV rules.
            if (singleKeyRuleTrigger.hasAny()) {
                singleKeyGestureDetector.interceptKey(it, false)
                if (singleKeyRuleTrigger.hasAnyWithInterceptMode(it.keyCode)
                    && singleKeyGestureDetector.isKeyIntercepted(
                        it.keyCode
                    )
                ) {
                    return@onKeyEvent false
                }
            }

            // Check Normal Rules.
            val fact = KeyEvent.newBuilder().setKeyCode(it.keyCode).build()
            if (shouldFire(getAllEnabledRules(), fact, false)) {
                if (it.action == android.view.KeyEvent.ACTION_UP) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    publishFact("KeyEvent", fact) { emptyMap() }
                }
                // False means consume.
                return@onKeyEvent false
            }
            return@onKeyEvent null
        }

        shortXContext.gesture.onGestureEvent {
            logger.d("onGestureEvent: $it")
            val fact = EdgeGesture.newBuilder().setEdge(it.first).setGesture(it.second).build()
            if (shouldFire(getAllEnabledRules(), fact, false)) {
                logger.d("hasAnyRuleMatchFact for onGestureEvent: $it")
                publishFact("Gesture", fact) { emptyMap() }
                return@onGestureEvent true
            }
            false
        }

        allTriggers.forEach { trigger ->
            kotlin.runCatching {
                logger.d("Register trigger: $trigger")
                trigger.register()
            }.onFailure {
                shortXContext.watchDog.onFatalError(
                    it,
                    "Fail register trigger $trigger"
                )
            }
        }
        logger.d("allTriggers registered.(or posted.)")
    }

    // Quick check.
    fun shouldFire(rules: List<Rule>, fact: ProtoMessage, checkCondition: Boolean): Boolean {
        return engine.shouldFire(rules = rules, fact = fact, checkCondition = checkCondition)
    }

    @Deprecated(
        "use publishFact with initialContextDataLazy",
        ReplaceWith("publishFact(factDefaultInstance, dataProvider)")
    )
    fun publishFact(
        reason: String,
        fact: ProtoMessage,
        initialContextData: Map<String, Any> = emptyMap()
    ) {
        publishFact(reason, fact) { initialContextData }
    }

    fun <T : ProtoMessage> publishFactChecked(
        factDefaultInstance: ProtoMessage,
        dataProvider: () -> FactPublishData<T>
    ) {
        val interceptRes = publishInterceptor.intercept(factDefaultInstance)
        if (!interceptRes) {
            val data = dataProvider()
            publishFact(data.reason, data.fact, data.initialContextDataLazy)
        } else {
            logger.i("publishFact ${factDefaultInstance.getTypeUrl()}, Skip intercepted.")
        }
    }

    fun publishFact(
        reason: String,
        fact: ProtoMessage,
        initialContextDataLazy: () -> Map<String, Any>
    ) {
        val publishTimeMillis = System.currentTimeMillis()
        handler.postSafety {
            logger.i("publishFact, ${fact::class.java}: $reason")
            logger.p("publishFact: $fact")
            val anyMatch = engine.fire(
                fact = fact,
                rules = getAllEnabledRules(),
                initialContextDataLazy = initialContextDataLazy
            )

            if (anyMatch) {
                factPublishRecorder.add(fact.toFactPublishRecord(publishTimeMillis))
            }

            notifyInternalFactListeners(fact, initialContextDataLazy)
        }
    }

    fun executeActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars = LocalVars.obtain(),
        parameterInputs: List<FuncParameterInput> = emptyList(),
        conflictStrategy: ConflictStrategy,
        onComplete: () -> Unit = {}
    ) {
        logger.d("executeActions: ${context.srcEvent} ${context.srcTitle}")
        if (actions.isNotEmpty()) {
            engine.evaluateActions(
                context = context,
                actions = actions,
                initialContextData = initialContextData,
                localVars = localVars,
                parameterInputs = parameterInputs,
                conflictStrategy = conflictStrategy,
                onComplete = onComplete,
            )
        }
    }

    @TestApi
    fun executeActionsBlocking(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap()
    ) {
        val latch = CountDownLatch(1)
        val listener = object : IJobStatusListener.Stub() {
            override fun onJobStarted(id: String?) {
            }

            override fun onJobCompleted(completion: String?) {
                if (context.srcId == completion) {
                    latch.countDown()
                }
            }

            override fun onJobCancelled(id: String?) {
            }
        }
        registerJobStatusListener(listener)
        executeActions(
            context = context,
            actions = actions,
            initialContextData = initialContextData,
            conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth
        )
        latch.await(60, TimeUnit.SECONDS)
        unregisterJobStatusListener(listener)
    }

    fun addOrUpdateRule(unMaskedRule: Rule) {
        // Check Subscribe state.
        val extras = unMaskedRule.extrasList
        val token = extras.firstOrNull { it.first == BuildPropInternal.APP_PACKAGE_NAME }
        logger.p("addOrUpdateRule, token: ${token?.second?.maskAll()}")
        // Check if has advanced action or facts

        var canAddRule = true
        val subOnlyModels = subscribedOnlyModels()

        if ((unMaskedRule.factsList
                    + unMaskedRule.conditionsList
                    + unMaskedRule.actionsList).any { actionProto ->
                subOnlyModels.any { actionProto is_ it.java }
            }
        ) {
            logger.p("This rule contains sub only model.")
            if (token == null || token.second.isNullOrEmpty()) {
                canAddRule = false
            }
        }

        if (!canAddRule) {
            logger.pe("Can not add rule.")
            ruleObs.broadcast {
                // "" means fail, need subscribed.
                kotlin.runCatching { it.onAddOrUpdate("") }
                    .onFailure { logger.e("Call onAddOrUpdate") }
            }
            return
        }

        // Must clear rule extras!
        val rule = Rule.newBuilder(unMaskedRule).clearExtras().build()

        val existing = getRuleById(rule.id)
        if (existing == null) {
            logger.d("onAddRule: ${rule.title}")
            // Add
            ruleSettingsStore.addRule(rule)
            handler.postSafety {
                allTriggers.forEach { it.onAddRule(rule) }
            }
        } else {
            logger.d("onUpdateRule: ${rule.title}")
            // Update
            ruleSettingsStore.addRule(rule)
            handler.postSafety {
                allTriggers.forEach { it.onUpdateRule(oldRule = existing, newRule = rule) }
            }
        }

        ruleObs.broadcast {
            kotlin.runCatching { it.onAddOrUpdate(rule.id) }
                .onFailure { logger.e("Call onAddOrUpdate") }
        }

        shortXContext.gesture.onRuleChanged()
    }

    fun deleteRule(ruleId: String) {
        // Disable first.
        setRuleEnabled(ruleId, false)

        val rules = ruleSettingsStore.deleteRule(ruleId)
        handler.postSafety {
            rules.forEach { r ->
                allTriggers.forEach { it.onDeleteRule(r) }
            }
        }

        ruleObs.broadcast {
            kotlin.runCatching { it.onDelete(ruleId) }
                .onFailure { logger.e("Call onDelete") }
        }

        shortXContext.gesture.onRuleChanged()
    }

    fun getRuleById(id: String): Rule? {
        return ruleSettingsStore.getRuleById(id)
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        logger.d("setRuleEnabled: $ruleId $enable")
        ruleSettingsStore.setRuleEnabled(ruleId, enable)
        ruleObs.broadcast {
            kotlin.runCatching {
                if (enable) {
                    it.onEnabled(ruleId)
                } else {
                    it.onDisabled(ruleId)
                }
            }.onFailure { logger.e("Call onEnabled/onDisabled") }
        }
        handler.postSafety {
            allTriggers.forEach {
                if (enable) it.onRuleEnabled(ruleId) else it.onRuleDisabled(ruleId)
            }
        }

        shortXContext.gesture.onRuleChanged()

        // Stop tasks
        handler.postSafety {
            engine.cancelJobs("Rule disabled(指令关闭)") {
                // Do not cancel RuleHook jobs.
                it.srcId == ruleId && !it.hasTag(ContextSrcEvent.ContextSrc_RuleHook)
            }
        }
    }

    // For App
    fun getActiveJobs(): List<String> = engine.activeJobs().map { it.srcId }

    // For App
    fun cancelJobs(jobs: List<String>, reason: String) {
        handler.postSafety {
            engine.cancelJobs(reason) {
                jobs.contains(it.srcId)
            }
        }
    }

    fun cancelAllJobs(reason: String) {
        cancelJobs(getActiveJobs(), reason)
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }

    fun getAllEnabledRules() = getAllRules().filter { it.isEnabled }

    fun getRuleCount() = ruleSettingsStore.getRuleCount()

    fun addDirectAction(unMaskedDA: DirectAction) {
        // Check Subscribe state.
        val extras = unMaskedDA.extrasList
        val token = extras.firstOrNull { it.first == BuildPropInternal.APP_PACKAGE_NAME }
        logger.p("addDirectAction, token: ${token?.second?.maskAll()}")
        // Check if has advanced action or facts

        var canAddRule = true
        val subOnlyModels = subscribedOnlyModels()

        if ((unMaskedDA.actionsList).any { actionProto ->
                subOnlyModels.any { actionProto is_ it.java }
            }
        ) {
            logger.p("This DA contains sub only model.")
            if (token == null || token.second.isNullOrEmpty()) {
                canAddRule = false
            }
        }

        if (!canAddRule) {
            logger.pe("Can not add DA.")
            daObs.broadcast {
                // "" means need subscribed.
                kotlin.runCatching { it.onAddOrUpdate("") }
                    .onFailure { logger.e("Call onAddOrUpdate") }
            }
            return
        }

        // Must clear rule extras!
        val da = DirectAction.newBuilder(unMaskedDA).clearExtras().build()

        logger.d("addDirectAction: $da")
        dasSettingsStore.addDirectAction(da)
        daObs.broadcast {
            kotlin.runCatching { it.onAddOrUpdate(da.id) }
                .onFailure { logger.e("Call onAddOrUpdate") }
        }
        handler.postSafety {
            allTriggers.forEach { it.onAddOrUpdateDA(da.id) }
        }
    }

    fun deleteDirectAction(id: String) {
        dasSettingsStore.deleteDirectAction(id)
        daObs.broadcast {
            kotlin.runCatching { it.onDelete(id) }
                .onFailure { logger.e("Call onDelete") }
        }
        handler.postSafety {
            allTriggers.forEach { it.onDeleteDA(id) }
        }

        // Stop tasks
        cancelJobs(getActiveJobs().filter { it == id }, "Rule deleted")
    }

    fun getDirectActionById(id: String): DirectAction? {
        return dasSettingsStore.getDirectActionById(id)
    }

    fun getAllDirectAction(): List<DirectAction> {
        return dasSettingsStore.getAllDirectActions()
    }

    fun getDirectActionCount() = dasSettingsStore.getDirectActionCount()

    fun executeDirectionActionById(
        context: EvaluateContext,
        id: String,
        parameterInputs: List<FuncParameterInput>
    ) {
        getDirectActionById(id)?.let { directAction ->
            executeActions(
                context = context,
                actions = directAction.actionsList,
                parameterInputs = parameterInputs.toMutableList().apply {
                    val addParams = directAction.parametersList
                        .filterNot { param ->
                            any { input -> param.name == input.name }
                        }
                        .map {
                            it.asDefaultInput()
                        }
                    logger.d("addParams: ${addParams.map { it.toLogString() }}")
                    addAll(addParams)
                },
                conflictStrategy = directAction.conflictPolicy
            )
        }
    }

    fun executeDirectionAction(
        context: EvaluateContext,
        directAction: DirectAction
    ) {
        executeActions(
            context = context,
            actions = directAction.actionsList,
            parameterInputs = directAction.parametersList.map {
                it.asDefaultInput()
            },
            conflictStrategy = directAction.conflictPolicy
        )
    }

    fun directExecuteRuleActions(context: EvaluateContext, ruleId: String) {
        val rule = getRuleById(ruleId)
        logger.d("directExecuteRuleActions: $ruleId ${rule?.title}")
        rule?.let { r ->
            val actions = r.actionsList
            executeActions(
                context = context,
                actions = actions,
                parameterInputs = r.parametersList.map { it.asDefaultInput() },
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth
            )
        }
    }

    fun addToggle(unMaskedToggle: Toggle) {
        // Check Subscribe state.
        val extras = unMaskedToggle.extrasList
        val token = extras.firstOrNull { it.first == BuildPropInternal.APP_PACKAGE_NAME }
        logger.p("addToggle, token: ${token?.second?.maskAll()}")
        // Check if has advanced action or facts

        // Must clear rule extras!
        val toggle = Toggle.newBuilder(unMaskedToggle).clearExtras().build()

        logger.d("addToggle: $toggle")
        toggleSettingsStore.addToggle(toggle)
        toggleObs.broadcast {
            kotlin.runCatching { it.onAddOrUpdate(toggle.id) }
                .onFailure { logger.e("Call onAddOrUpdate") }
        }
    }

    // Note: This API may block current thread.
    fun isToggleEnabled(id: String): Boolean {
        val toggle = getToggleById(id)
        if (toggle == null) {
            logger.e("Toggle with id: $id not exists.")
            return false
        }
        val stateHandle = toggle.stateHandle
        if (stateHandle is_ ToggleStateHandleByStore::class.java) {
            return store.read(Settings.BoolSettings.EnableToggle(id))
        } else if (stateHandle is_ ToggleStateHandleByCondition::class.java) {
            val byCondition = stateHandle unpack_ ToggleStateHandleByCondition::class.java
            val conditions = byCondition.conditionsList
            if (conditions.isEmpty()) {
                return false
            }
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                .setSrcTitle(toggle.title)
                .build()
            return engine.evaluateConditions(
                evaluateContext,
                byCondition.condOp,
                byCondition.condOpPayload,
                byCondition.conditionsList,
                emptyMap()
            )
        } else {
            return false
        }
    }

    fun setToggleEnabled(id: String, enable: Boolean) {
        logger.d("setToggleEnabled: $id $enable")
        val toggle = getToggleById(id)
        if (toggle == null) {
            logger.e("Toggle with id: $id not exists.")
            return
        }
        val stateHandle = toggle.stateHandle
        if (stateHandle is_ ToggleStateHandleByStore::class.java) {
            store.write(Settings.BoolSettings.EnableToggle(id), enable)
            toggleObs.broadcast {
                kotlin.runCatching {
                    it.onEnableStateMayChange(id)
                }.onFailure { logger.e("Call onEnabled/onDisabled") }
            }
        } else {
            // Noop.
        }
        val evaluateContext = EvaluateContext.newBuilder()
            .setSrcId(id)
            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
            .setSrcTitle(toggle.title)
            .build()
        executeActions(
            context = evaluateContext,
            actions = enable.select(
                { toggle.actionsOnEnabledList },
                { toggle.actionsOnDisabledList }),
            conflictStrategy = ConflictStrategy.ConflictStrategy_ReplaceOld,
            onComplete = {
                logger.d("Toggle actions execute complete, broadcast onEnableStateMayChange")
                toggleObs.broadcast {
                    kotlin.runCatching {
                        it.onEnableStateMayChange(id)
                    }.onFailure { logger.e("Call onEnabled/onDisabled") }
                }
            }
        )
    }

    fun deleteToggle(id: String) {
        toggleSettingsStore.deleteToggle(id)
        toggleObs.broadcast {
            kotlin.runCatching { it.onDelete(id) }
                .onFailure { logger.e("Call onDelete") }
        }
    }

    fun getToggleById(id: String): Toggle? {
        return toggleSettingsStore.getToggleById(id)
    }

    fun getAllToggles(): List<Toggle> {
        return toggleSettingsStore.getAllToggles()
    }

    fun getToggleCount(): Int {
        return toggleSettingsStore.getToggleCount()
    }

    fun registerToggleObs(obs: IToggleObserver) {
        toggleObs.register(obs)
    }

    fun unregisterToggleObs(obs: IToggleObserver) {
        toggleObs.unregister(obs)
    }

    fun executeShell(command: String, callback: ICallback): ICancellationSignal {
        val signal = CancellationSignal.createTransport()
        val action = tornaco.apps.shortx.core.proto.action.ShellCommand.newBuilder()
            .setCommand(command)
            .setSingleShot(true)
            .setId("UI#executeShellCommand")
            .build()

        @Suppress("ObjectLiteralToLambda") val callbackDeadReceipt =
            object : IBinder.DeathRecipient {
                @DoNotStrip
                override fun binderDied() {
                    logger.w("executeShell, callback binderDied.")
                    signal.cancel()
                }
            }

        val asBinder = callback.asBinder()
        val job = systemServiceScope.launchSafety {
            val handleParams = dummyHandlerParamsForAction(
                androidContext = context,
                shortXContext = shortXContext,
                evaluateContext = EvaluateContext.newBuilder()
                    .setSrcId(UUID.randomUUID().toString())
                    .setSrcTitle("Testing Shell")
                    .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                    .setSerialNumber(nextEvaluateContextSerialNumber())
                    .build(),
                action = action.pack_()
            )
            runCatching {
                ShellCommand.handle(handleParams)
                val code =
                    handleParams.contextData.get(ContextDataMapping.ShellCommand.shellCode.name)
                        .toString().toInt()
                val out =
                    handleParams.contextData.get(ContextDataMapping.ShellCommand.shellOut.name)
                        .toString()
                val error =
                    handleParams.contextData.get(ContextDataMapping.ShellCommand.shellErr.name)
                        .toString()


                runCatching {
                    callback.callIfAlive {
                        callback.receive(
                            ByteArrayWrapper(
                                ShellRes.newBuilder()
                                    .setCode(code)
                                    .addAllOut(listOf(out))
                                    .addAllErr(listOf(error))
                                    .build()
                                    .toByteArray()
                            )
                        )
                    }
                }
            }.onFailure { err ->
                logger.e(err, "ShellCommand")
                callback.callIfAlive {
                    callback.receive(
                        ByteArrayWrapper(
                            ShellRes.newBuilder()
                                .setCode(-1)
                                .addAllErr(listOf(err.stackTraceToString()))
                                .build()
                                .toByteArray()
                        )
                    )
                }
            }

            asBinder.unlinkToDeath(callbackDeadReceipt, 0)
        }
        asBinder.linkToDeath(callbackDeadReceipt, 0)
        CancellationSignal.fromTransport(signal).setOnCancelListener {
            logger.i("CancellationSignal cancel.")
            job.cancel(CancellationException("User canceled(用户手动取消)"))
        }
        return signal
    }

    fun executeMVEL(expression: String): String {
        var res = ""
        runBlocking {
            systemServiceScope.launchSafety {
                val handleParams = dummyHandlerParamsForAction(
                    androidContext = context,
                    shortXContext = shortXContext,
                    evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(UUID.randomUUID().toString())
                        .setSrcTitle("Testing MVEL")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build(),
                    action = tornaco.apps.shortx.core.proto.action.ExecuteMVEL.newBuilder()
                        .setExpression(expression).build().pack_()
                )
                runCatching {
                    ExecuteMVEL.handle(handleParams)
                    res =
                        handleParams.contextData.get(ContextDataMapping.ExecuteMVEL.mvelRet.name)
                            .toString()
                }.onFailure {
                    res = Log.getStackTraceString(it)
                }
            }.join()
        }
        return res
    }

    fun executeJS(expression: String): String {
        var res = ""
        runBlocking {
            systemServiceScope.launchSafety {
                val handleParams = dummyHandlerParamsForAction(
                    androidContext = context,
                    shortXContext = shortXContext,
                    evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(UUID.randomUUID().toString())
                        .setSrcTitle("Testing JS")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build(),
                    action = tornaco.apps.shortx.core.proto.action.ExecuteJS.newBuilder()
                        .setExpression(expression).build().pack_()
                )
                runCatching {
                    tornaco.apps.shortx.services.rule.actor.impl.ExecuteJS.handle(handleParams)
                    res =
                        handleParams.contextData.get(ContextDataMapping.ExecuteJS.jsRet.name)
                            .toString()
                }.onFailure {
                    res = Log.getStackTraceString(it)
                }
            }.join()
        }
        return res
    }

    fun evaluateCondition(
        context: EvaluateContext,
        condition: ProtoAny,
    ): Boolean {
        return engine.evaluateConditions(
            context = context,
            condOp = ConditionOperator.ALL,
            condOpPayload = ConditionOperatorPayload.newBuilder().build(),
            conditions = listOf(condition),
            initialContextData = emptyMap()
        )
    }

    fun registerJobStatusListener(listener: IJobStatusListener) {
        jobStatusListeners.register(listener)
    }

    fun unregisterJobStatusListener(listener: IJobStatusListener) {
        jobStatusListeners.unregister(listener)
    }

    fun registerActionEvaluateListener(listener: IActionEvaluateListener) {
        actionEvaluateListener.register(listener)
    }

    fun unregisterActionEvaluateListener(listener: IActionEvaluateListener) {
        actionEvaluateListener.unregister(listener)
    }

    fun addGlobalVar(gv: GlobalVar) {
        globalVarStore.addGlobalVar(gv)
    }

    fun deleteGlobalVar(name: String) {
        globalVarStore.deleteGlobalVar(name)
    }

    fun getGlobalVarByName(name: String): GlobalVar? {
        return globalVarStore.getGlobalVarByName(name)
    }

    fun getAllGlobalVars(): List<GlobalVar> {
        return globalVarStore.getAllGlobalVars()
    }

    fun registerGlobalVarObs(obs: IGlobalVarObserver) {
        globalVarObs.register(obs)
    }

    fun unregisterGlobalVarObs(obs: IGlobalVarObserver) {
        globalVarObs.unregister(obs)
    }

    fun getActionEvaluateRecords(): List<ActionEvaluateRecord> {
        return actionEvaluateRecorder.actionEvaluateRecords
    }

    fun getEvaluatingActions(): List<String> {
        return actionEvaluateRecorder.evaluatingActions
    }

    fun clearActionEvaluateRecords() {
        actionEvaluateRecorder.clear()
    }

    fun getConditionEvaluateRecords(): List<ConditionEvaluateRecord> {
        return conditionEvaluateRecorder.conditionEvaluateRecords
    }

    fun clearConditionEvaluateRecords() {
        conditionEvaluateRecorder.clear()
    }

    fun getFactPublishRecords(): List<FactPublishRecord> {
        return factPublishRecorder.records
    }

    fun clearFactPublishRecords() {
        factPublishRecorder.clear()
    }

    fun clearEvaluateRecords() {
        clearFactPublishRecords()
        clearConditionEvaluateRecords()
        clearActionEvaluateRecords()
    }

    fun isEvaluateRecordEnabled(id: String): Boolean {
        return store.read(Settings.BoolSettings.EnableEvaluateRecorder(id))
    }

    fun setEvaluateRecordEnabled(id: String, enable: Boolean) {
        store.write(Settings.BoolSettings.EnableEvaluateRecorder(id), enable)
    }

    fun setForceDisableShortXByKeyPressEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableForceDisableShortXByKeyPress, enable)
    }

    fun isForceDisableShortXByKeyPressEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableForceDisableShortXByKeyPress)
    }

    fun registerRuleObs(obs: IRuleObserver) {
        ruleObs.register(obs)
    }

    fun unregisterRuleObs(obs: IRuleObserver) {
        ruleObs.unregister(obs)
    }

    fun registerDAObs(obs: IDAObserver) {
        daObs.register(obs)
    }

    fun unregisterDAObs(obs: IDAObserver) {
        daObs.unregister(obs)
    }

    fun setKeyEventPromptEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableKeyEventPrompt, enable)
    }

    fun isKeyEventPromptEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableKeyEventPrompt)
    }

    fun setRuleEngineEnabled(engine: String, enable: Boolean) {
        store.write(Settings.BoolSettings.EnableRuleEngine(engine, false), enable)
    }

    fun isRuleEngineEnabled(engine: String): Boolean {
        val defaultValue = engine != RuleEngineNames.ENGINE_NAME_SMS
        return store.read(Settings.BoolSettings.EnableRuleEngine(engine, defaultValue))
    }

    fun addOrUpdateRuleSet(rs: RuleSet) {
        val exists = ruleSetsStore.getRuleSet(rs.id)
        if (exists == null) {
            logger.d("Add RuleSet: ${rs.title}")
            ruleSetsStore.addRuleSet(rs)
        } else {
            logger.d("Update RuleSet: ${rs.title}")
            ruleSetsStore.addRuleSet(rs)
        }
        ruleObs.broadcast {
            kotlin.runCatching {
                it.onSetAddOrUpdate(rs.id)
            }.onFailure { logger.e("Call onSetAddOrUpdate") }
        }
    }

    fun deleteRuleSet(id: String) {
        ruleSetsStore.deleteRuleSet(id)
        ruleObs.broadcast {
            kotlin.runCatching {
                it.onSetDelete(id)
            }.onFailure { logger.e("Call onSetDelete") }
        }
    }

    fun getRuleSetById(id: String, withRules: Boolean): RuleSet? {
        return ruleSetsStore.getRuleSet(id)?.let { ruleSet ->
            if (withRules) {
                RuleSet
                    .newBuilder(ruleSet)
                    .setRuleList(
                        RuleList.newBuilder()
                            .addAllRules(getAllRules().filter { it.ruleSetId == id })
                            .build()
                    ).build()
            } else {
                ruleSet
            }
        }
    }

    fun getAllRuleSets(withRules: Boolean): List<RuleSet> {
        return ruleSetsStore.getAllRuleSets()
    }

    fun addOrUpdateDASet(ds: DirectActionSet) {
        val exists = daSetsStore.getDirectActionSet(ds.id)
        if (exists == null) {
            logger.d("Add DirectActionSet: ${ds.title}")
            daSetsStore.addDirectActionSet(ds)
        } else {
            logger.d("Update DirectActionSet: ${ds.title}")
            daSetsStore.addDirectActionSet(ds)
        }
        daObs.broadcast {
            kotlin.runCatching {
                it.onSetAddOrUpdate(ds.id)
            }.onFailure { logger.e("Call onSetAddOrUpdate") }
        }
    }

    fun deleteDASet(id: String) {
        daSetsStore.deleteDirectActionSet(id)
        daObs.broadcast {
            kotlin.runCatching {
                it.onSetDelete(id)
            }.onFailure { logger.e("Call onSetDelete") }
        }
    }

    fun getDASetById(id: String, withDAs: Boolean): DirectActionSet? {
        return daSetsStore.getDirectActionSet(id)?.let { directActionSet ->
            if (withDAs) {
                DirectActionSet.newBuilder(directActionSet)
                    .setDaList(
                        DirectActionList
                            .newBuilder()
                            .addAllDirectActions(getAllDirectAction().filter { it.directActionSetId == id })
                            .build()
                    )
                    .build()
            } else {
                directActionSet
            }
        }
    }

    fun getAllDASets(): List<DirectActionSet> {
        return daSetsStore.getAllDirectActionSets()
    }

    fun shortcutAppLaunched(packageName: String) {
        logger.d("shortcutAppLaunched: $packageName")
    }

    fun setRuleFeatureEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableRuleFeature, enable)
        if (!enable) {
            shortXContext.wm.ensureAutomationDisconnected("Disable Rule feature")
        }
    }

    fun isRuleFeatureEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableRuleFeature)
    }

    fun setHttpRequestLogEnabled(enabled: Boolean) {
        store.write(Settings.BoolSettings.EnableHttpRequestLog, enabled)
        HttpRequestLogging.isLogEnabled = enabled
    }

    fun isHttpRequestLogEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableHttpRequestLog)
    }

    fun getHttpRequestLogFD(): ParcelFileDescriptor? {
        return kotlin.runCatching {
            ParcelFileDescriptor.open(
                HttpRequestLogging.logFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        }.getOrElse {
            logger.e(it, "getLogFD error")
            null
        }
    }

    fun getHttpRequestLogPath(): String {
        return HttpRequestLogging.logFile.absolutePath
    }

    fun clearHttpRequestLogs() {
        HttpRequestLogging.clearLogs()
    }

    override fun screenOn() {
        super.screenOn()
        logger.d("onScreenOn")
        shortXContext.rule.publishFact(
            "onScreenOn",
            ScreenOn.newBuilder().build()
        ) { emptyMap() }
    }

    override fun screenOff() {
        super.screenOff()
        logger.d("screenOff")
        shortXContext.rule.publishFact(
            "onScreenOff",
            ScreenOff.newBuilder().build()
        ) { emptyMap() }
    }

    internal fun registerInternalFactListener(listener: InternalFactListener) {
        synchronized(internalFactListeners) {
            internalFactListeners.add(listener)
        }
    }

    internal fun unregisterInternalFactListener(listener: InternalFactListener) {
        synchronized(internalFactListeners) {
            internalFactListeners.remove(listener).also {
                if (it) logger.d("did unregisterInternalFactListener: ${listener.usage}")
            }
        }
    }

    internal fun unregisterInternalFactListener(usage: String) {
        synchronized(internalFactListeners) {
            internalFactListeners.removeIf { it.usage == usage }.also {
                if (it) logger.d("did unregisterInternalFactListener: $usage")
            }
        }
    }

    private fun notifyInternalFactListeners(
        fact: ProtoMessage,
        initialContextDataLazy: () -> Map<String, Any>
    ) {
        systemServiceScope.launchSafety {
            val listeners: Array<InternalFactListener> = synchronized(internalFactListeners) {
                internalFactListeners.toTypedArray()
            }
            withContext(Dispatchers.IO) {
                listeners.forEach { it.onFact(fact, initialContextDataLazy) }
            }
        }
    }

    private fun forceDisableShortX() {
        systemServiceScope.launchSafety {
            setRuleFeatureEnabled(false)
            cancelJobs(getActiveJobs(), "Force disable ShortX")

            shortXContext.notification.post(
                PostNotification.newBuilder()
                    .setTitle(i18NIns["ui.shortx.force.disabled"])
                    .setMessage(i18NIns["ui.shortx.force.disabled.message"])
                    .setId("ShortX.Rule")
                    .setIsImportant(true)
                    .setSmallIcon(Remix.System.forbid_2_fill)
                    .addClickAction(
                        LaunchApp.newBuilder()
                            .setId("ShortX.Rule")
                            .setAppPkg(
                                AppPkg.newBuilder().setUserId(myUserId)
                                    .setPkgName(BuildPropInternal.APP_PACKAGE_NAME).build()
                            )
                            .build().pack_()
                    )
                    .build()
            )
        }
    }
}