package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.KeyEvent.KEYCODE_POWER
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IForceDisableListener
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.IRuleFeatureStateListener
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.input.InternalInputFlags.FLAG_SHORTX_INJECTION
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.CancellationSignal
import tornaco.apps.shortx.core.os.ICancellationSignal
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema
import tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionList
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.func.`Function` as ShortXFunction
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.getTypeUrl
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.quitFacts
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleEditOp
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
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
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
import tornaco.apps.shortx.services.data.WebDavProfileStore
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.actor.ActionEvaluateRecorder
import tornaco.apps.shortx.services.rule.actor.PrebuiltActor
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteMVEL
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequestLogging
import tornaco.apps.shortx.services.rule.actor.impl.ShellCommand
import tornaco.apps.shortx.services.rule.actor.impl.js.ExecuteJS
import tornaco.apps.shortx.services.rule.actor.impl.js.JSLogging
import tornaco.apps.shortx.services.rule.actor.toActionEvaluateRecord
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluateRecorder
import tornaco.apps.shortx.services.rule.cond.PrebuiltCond
import tornaco.apps.shortx.services.rule.cond.toConditionEvaluateRecord
import tornaco.apps.shortx.services.rule.fact.FactPublishRecorder
import tornaco.apps.shortx.services.rule.fact.toFactPublishRecord
import tornaco.apps.shortx.services.rule.fact.trigger.KeyPressTimesInIntervalListener
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.rule.systemenv.SystemEnvInit
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.callIfAlive
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

typealias AndroidKeyEvent = android.view.KeyEvent

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

    private val webDavProfileStore: WebDavProfileStore by lazy {
        WebDavProfileStore(
            scope = systemServiceScope,
            encKey = requireNotNull(store.read(Settings.StringSettings.ServiceIns)) {
                "ServiceIns is null"
            })
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

    val keyGestureController by lazy {
        KeyGestureController(context, shortXContext, handler)
    }

    private val allTriggers by lazy {
        allTriggers(context, shortXContext).apply {
            // These are internal Triggers.
            with(keyGestureController) {
                add(singleKeyRuleTrigger)
                add(keyCombinationTrigger)
                logger.w("allTriggers size: $size")
            }
        }
    }

    private val jobStatusListeners = RemoteCallbackListExt<IJobStatusListener>()
    private val actionEvaluateListener = RemoteCallbackListExt<IActionEvaluateListener>()
    private val forceDisableListeners = RemoteCallbackListExt<IForceDisableListener>()
    private val ruleFeatureStateListeners = RemoteCallbackListExt<IRuleFeatureStateListener>()

    private val globalVarObs = RemoteCallbackListExt<IGlobalVarObserver>()
    private val ruleObs = RemoteCallbackListExt<IRuleObserver>()
    private val daObs = RemoteCallbackListExt<IDAObserver>()
    private val toggleObs = RemoteCallbackListExt<IToggleObserver>()

    private val actionEvaluateRecorder = ActionEvaluateRecorder()
    private val conditionEvaluateRecorder = ConditionEvaluateRecorder()
    private val factPublishRecorder = FactPublishRecorder()

    // For internal usage, such as Actor listen to a fact to act.
    private val internalFactListeners: MutableList<InternalFactListener> = mutableListOf()

    private val forceDisableShortXListener = object : KeyPressTimesInIntervalListener(12, 5000) {
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
            androidUiContext = uiContext,
            handler = handler,
            onJobStarted = { id ->
                systemServiceScope.launchSafety("onJobStarted") {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobStarted(id) }
                            .onFailure { logger.e(it, "call onJobStarted") }
                    }
                }
            },
            onJobCompleted = { id ->
                systemServiceScope.launchSafety("onJobCompleted") {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobCompleted(id) }
                            .onFailure { logger.e(it, "call onJobCompleted") }
                    }
                }
            },
            onJobCancelled = { id ->
                systemServiceScope.launchSafety("onJobCancelled") {
                    jobStatusListeners.broadcast {
                        kotlin.runCatching { it.onJobCancelled(id) }
                            .onFailure { logger.e(it, "call onJobCancelled") }
                    }
                }
            },
            actionEvaluateStartListener = { actionId ->
                logger.d("ActionLifecycle actionEvaluateStartListener: $actionId")
                actionEvaluateRecorder.addEvaluating(actionId)
                systemServiceScope.launchSafety("actionEvaluateStartListener") {
                    actionEvaluateListener.broadcast {
                        kotlin.runCatching { it.onStart(actionId) }
                            .onFailure { logger.e(it, "call onStart") }
                    }
                }
            },
            actionEvaluateResultListener = { result ->
                logger.d {
                    "ActionLifecycle actionEvaluateResultListener: ${result.actionId} ${result.action.typeUrl} ${result.evaluateContext.srcTitle}"
                }
                actionEvaluateRecorder.removeEvaluating(result.actionId)
                systemServiceScope.launchSafety("actionEvaluateResultListener") {
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

    val publishInterceptor by lazy {
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
        webDavProfileStore.readBlocking()

        installListeners()

        SystemEnvInit.install(context, shortXContext)

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

    fun interceptKeyBeforeQueueing(event: AndroidKeyEvent, interactive: Boolean): Boolean? {
        logger.d("onKeyEvent up for keyCode: ${event.keyCode}, interactive :$interactive")

        if (event.action == AndroidKeyEvent.ACTION_UP && isKeyEventPromptEnabled()) {
            handler.postSafety("Toast Key") {
                Toast.makeText(context, "Key code: ${event.keyCode}", Toast.LENGTH_SHORT).show()
            }
        }

        if (event.keyCode == KEYCODE_POWER) {
            if (event.action == AndroidKeyEvent.ACTION_DOWN) {
                forceDisableShortXListener.onKeyPressed()
            }
        } else {
            forceDisableShortXListener.onOtherKeyPressed()
        }

        // Check if event accessibility service.
        if ((event.flags and FLAG_SHORTX_INJECTION) != 0) {
            logger.logOnDebug("InternalInputFlags.FLAG_SHORTX_INJECTION event: $event")
            return null
        }

        if (keyGestureController.consumeKeyEvent(event, interactive)) {
            return true
        }

        // Check Normal Rules.
        val fact = KeyEvent.newBuilder().setKeyCode(event.keyCode).build()
        if (shouldFire(
                rules = getAllEnabledRules(),
                das = getAllDirectAction(),
                fact = fact,
                checkCondition = false
            )
        ) {
            if (event.action == AndroidKeyEvent.ACTION_UP) {
                logger.d("hasAnyRuleMatchFact for keyCode: ${event.keyCode}")
                publishFact("KeyEvent", fact) { emptyMap() }
            }
            return false
        }

        // END. fallback result.
        return null
    }

    // Quick check.
    fun shouldFire(
        fact: ProtoMessage,
        checkCondition: Boolean = false,
        rules: List<Rule> = getAllEnabledRules(),
        das: List<DirectAction> = getAllDirectAction(),
        excludeFacts: Set<String> = setOf(AnyFact.getDefaultInstance().pack_().typeUrl)
    ): Boolean {
        return engine.shouldFire(
            rules = rules,
            das = das,
            fact = fact,
            checkCondition = checkCondition,
            excludeFacts = excludeFacts
        )
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
            logger.logOnDebug("publishFact ${factDefaultInstance.getTypeUrl()}, Skip intercepted.")
        }
    }

    fun publishFact(
        reason: String,
        fact: ProtoMessage,
        initialContextDataLazy: () -> Map<String, Any>
    ) {
        val publishTimeMillis = System.currentTimeMillis()
        handler.postSafety("publishFact") {
            val startTime = System.currentTimeMillis()
            logger.logOnDebug("publishFact, ${fact::class.java}: $reason")
            logger.logOnDebug("publishFact: $fact")
            val anyMatch = engine.fire(
                fact = fact,
                rules = getAllEnabledRules(),
                das = getAllDirectAction(),
                initialContextDataLazy = initialContextDataLazy
            )

            if (anyMatch) {
                factPublishRecorder.add(fact.toFactPublishRecord(publishTimeMillis))
            }

            notifyInternalFactListeners(fact, initialContextDataLazy)
            val takenTime = System.currentTimeMillis() - startTime
            logger.d("publishFact, take time: $takenTime ms")
        }
    }

    fun executeActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        actionAsyncMode: ActionAsyncMode,
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars = LocalVars.obtain(),
        parameterInputs: List<FuncParameterInput> = emptyList(),
        conflictStrategy: ConflictStrategy,
        onComplete: ((contextData: Map<String, Any?>) -> Unit)? = null
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
                actionAsyncMode = actionAsyncMode
            )
        }
    }

    fun executeActionsBlocking(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars = LocalVars.obtain(),
        parameterInputs: List<FuncParameterInput> = emptyList(),
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
            localVars = localVars,
            parameterInputs = parameterInputs,
            conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
            actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
        )
        latch.await(60, TimeUnit.SECONDS)
        unregisterJobStatusListener(listener)
    }

    fun addOrUpdateRule(unMaskedRule: Rule) {
        // Check Subscribe state.
        val extras = unMaskedRule.extrasList
        val token = extras.firstOrNull { it.first == BuildPropInternal.APP_PACKAGE_NAME }
        logger.logOnDebug("addOrUpdateRule, token: ${token?.second?.maskAll()}")
        // Check if has advanced action or facts

        var canAddRule = true
        val subOnlyModels = subscribedOnlyModels()

        if ((unMaskedRule.factsList
                    + unMaskedRule.conditionsList
                    + unMaskedRule.actionsList).any { actionProto ->
                subOnlyModels.any { actionProto is_ it.java }
            }
        ) {
            logger.logOnDebug("This rule contains sub only model.")
            if (token == null || token.second.isNullOrEmpty()) {
                canAddRule = false
            }
        }

        if (!canAddRule) {
            logger.logOnDebug("Can not add rule.")
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
            handler.postSafety("Call onAddRule") {
                allTriggers.forEach { it.onAddRule(rule) }
            }
        } else {
            logger.d("onUpdateRule: ${rule.title}")
            // Update
            ruleSettingsStore.addRule(rule)
            handler.postSafety("Call onUpdateRule") {
                allTriggers.forEach { it.onUpdateRule(oldRule = existing, newRule = rule) }
            }
        }

        ruleObs.broadcast {
            kotlin.runCatching { it.onAddOrUpdate(rule.id) }
                .onFailure { logger.e("Call onAddOrUpdate") }
        }
    }

    fun deleteRule(ruleId: String) {
        // Disable first.
        setRuleEnabled(ruleId, false)

        val rules = ruleSettingsStore.deleteRule(ruleId)
        handler.postSafety("Call onDeleteRule") {
            rules.forEach { r ->
                allTriggers.forEach { it.onDeleteRule(r) }
            }
        }

        ruleObs.broadcast {
            kotlin.runCatching { it.onDelete(ruleId) }
                .onFailure { logger.e("Call onDelete") }
        }
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
        handler.postSafety("Call onRuleEnabled") {
            allTriggers.forEach {
                if (enable) it.onRuleEnabled(ruleId) else it.onRuleDisabled(ruleId)
            }
        }

        // Stop tasks on disable.
        if (!enable) {
            handler.postSafety("cancelJobs") {
                engine.cancelJobs("Rule disabled(指令关闭)") {
                    // Do not cancel RuleHook-Disable jobs.
                    it.srcId == ruleId && (!it.hasTag(ContextSrcEvent.ContextSrc_RuleHook) || it.hasTag(
                        RuleEditOp.RuleEditOp_Enable.name
                    ))
                }
            }
        }
    }

    // For App
    fun getActiveJobs(): List<String> = engine.activeJobs().map { it.srcId }

    // For App
    fun cancelJobs(jobs: List<String>, reason: String) {
        handler.postSafety("cancelJobs") {
            engine.cancelJobs(reason) {
                jobs.contains(it.srcId)
            }
        }

        handler.postSafety("Call onRuleStopped") {
            jobs.forEach { jobId ->
                allTriggers.forEach {
                    it.onRuleOrDAStopped(jobId)
                }
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
        logger.logOnDebug("addDirectAction, token: ${token?.second?.maskAll()}")
        // Check if has advanced action or facts

        var canAddRule = true
        val subOnlyModels = subscribedOnlyModels()

        if ((unMaskedDA.actionsList).any { actionProto ->
                subOnlyModels.any { actionProto is_ it.java }
            }
        ) {
            logger.logOnDebug("This DA contains sub only model.")
            if (token == null || token.second.isNullOrEmpty()) {
                canAddRule = false
            }
        }

        if (!canAddRule) {
            logger.logOnDebug("Can not add DA.")
            daObs.broadcast {
                // "" means need subscribed.
                kotlin.runCatching { it.onAddOrUpdate("") }
                    .onFailure { logger.e("Call onAddOrUpdate") }
            }
            return
        }

        // Must clear rule extras!
        val da = DirectAction.newBuilder(unMaskedDA).clearExtras().build()

        val existing = getDirectActionById(da.id)

        if (existing == null) {
            logger.d("addDirectAction: $da")
            dasSettingsStore.addDirectAction(da)
            daObs.broadcast {
                kotlin.runCatching { it.onAddOrUpdate(da.id) }
                    .onFailure { logger.e("Call onAddOrUpdate") }
            }
            handler.postSafety("Call onAddDA") {
                allTriggers.forEach { it.onAddDA(da) }
            }
        } else {
            logger.d("updateDirectAction: $da")
            dasSettingsStore.addDirectAction(da)
            daObs.broadcast {
                kotlin.runCatching { it.onAddOrUpdate(da.id) }
                    .onFailure { logger.e("Call onAddOrUpdate") }
            }
            handler.postSafety("Call onUpdateDA") {
                allTriggers.forEach { it.onUpdateDA(existing, da) }
            }
        }
    }

    fun deleteDirectAction(id: String) {
        val existing = dasSettingsStore.getDirectActionById(id)
        if (existing != null) {
            dasSettingsStore.deleteDirectAction(id)
            daObs.broadcast {
                kotlin.runCatching { it.onDelete(id) }
                    .onFailure { logger.e("Call onDelete") }
            }
            handler.postSafety("Call onDeleteDA") {
                allTriggers.forEach { it.onDeleteDA(existing) }
            }

            // Stop tasks
            cancelJobs(getActiveJobs().filter { it == id }, "Rule deleted")
        } else {
            logger.e("deleteDirectAction, da not found: $id")
        }
    }

    fun getDirectActionById(id: String): DirectAction? {
        return dasSettingsStore.getDirectActionById(id)
    }

    fun getAllDirectAction(): List<DirectAction> {
        return dasSettingsStore.getAllDirectActions()
    }

    fun getDirectActionCount() = dasSettingsStore.getDirectActionCount()

    private fun functionManagementNotImplemented(): Nothing {
        throw UnsupportedOperationException(
            "Function management is not implemented yet. Complete function store tasks first."
        )
    }

    fun addFunction(function: ShortXFunction) {
        logger.w("addFunction is not implemented yet: ${function.id}")
        functionManagementNotImplemented()
    }

    fun deleteFunction(id: String) {
        logger.w("deleteFunction is not implemented yet: $id")
        functionManagementNotImplemented()
    }

    fun getFunctionById(id: String): ShortXFunction? {
        logger.w("getFunctionById is not implemented yet: $id")
        return functionManagementNotImplemented()
    }

    fun getAllFunctions(): List<ShortXFunction> {
        logger.w("getAllFunctions is not implemented yet")
        return functionManagementNotImplemented()
    }

    fun executeDirectionActionById(
        context: EvaluateContext,
        id: String,
        parameterInputs: List<FuncParameterInput>
    ) {
        getDirectActionById(id)?.let { directAction ->
            executeDirectAction(context, directAction, parameterInputs)
        }
    }

    fun executeDirectAction(
        context: EvaluateContext,
        directAction: DirectAction,
        parameterInputs: List<FuncParameterInput> = emptyList(),
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars = LocalVars.obtain(),
    ) {
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
            initialContextData = initialContextData,
            localVars = localVars,
            conflictStrategy = directAction.conflictPolicy,
            actionAsyncMode = directAction.actionAsyncMode
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
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                actionAsyncMode = r.actionAsyncMode
            )
        }
    }

    fun addToggle(unMaskedToggle: Toggle) {
        // Check Subscribe state.
        val extras = unMaskedToggle.extrasList
        val token = extras.firstOrNull { it.first == BuildPropInternal.APP_PACKAGE_NAME }
        logger.logOnDebug("addToggle, token: ${token?.second?.maskAll()}")
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
            actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync,
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
        val job = systemServiceScope.launchSafety("executeShell.job") {
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

    fun executeMVEL(expression: String, coroutineContext: CoroutineContext): String {
        var res = ""
        runBlocking {
            systemServiceScope.launchSafety("executeMVEL.runBlocking") {
                val handleParams = dummyHandlerParamsForAction(
                    androidContext = context,
                    shortXContext = shortXContext,
                    evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(UUID.randomUUID().toString())
                        .setSrcTitle("Testing MVEL")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build(),
                    action = tornaco.apps.shortx.core.proto.action.ExecuteMVEL
                        .newBuilder()
                        .setExpression(expression)
                        .setContext(coroutineContext)
                        .build().pack_()
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

    fun executeJS(expression: String, coroutineContext: CoroutineContext): String {
        var res = ""
        runBlocking {
            systemServiceScope.launchSafety("executeJS.runBlocking") {
                val handleParams = dummyHandlerParamsForAction(
                    androidContext = context,
                    shortXContext = shortXContext,
                    evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(UUID.randomUUID().toString())
                        .setSrcTitle("Testing JS")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build(),
                    action = tornaco.apps.shortx.core.proto.action.ExecuteJS
                        .newBuilder()
                        .setExpression(expression)
                        .setContext(coroutineContext)
                        .build().pack_()
                )
                runCatching {
                    ExecuteJS.handle(handleParams)
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
        // 通知所有注册的ForceDisable监听器
        forceDisableListeners.broadcast {
            kotlin.runCatching { it.onForceDisableStateChanged(enable) }
                .onFailure { logger.e(it, "Call onForceDisableStateChanged") }
        }
    }

    fun isForceDisableShortXByKeyPressEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableForceDisableShortXByKeyPress)
    }

    fun registerForceDisableListener(listener: IForceDisableListener) {
        forceDisableListeners.register(listener)
    }

    fun unregisterForceDisableListener(listener: IForceDisableListener) {
        forceDisableListeners.unregister(listener)
    }

    fun registerRuleFeatureStateListener(listener: IRuleFeatureStateListener) {
        ruleFeatureStateListeners.register(listener)
    }

    fun unregisterRuleFeatureStateListener(listener: IRuleFeatureStateListener) {
        ruleFeatureStateListeners.unregister(listener)
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
        
        // 通知监听器规则功能状态已改变
        systemServiceScope.launchSafety("notifyRuleFeatureStateChanged") {
            ruleFeatureStateListeners.broadcast { ie ->
                kotlin.runCatching { ie.onRuleFeatureStateChanged(enable) }
                    .onFailure { logger.e(it, "call onRuleFeatureStateChanged") }
            }
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

    fun getJSLogFD(): ParcelFileDescriptor? {
        return kotlin.runCatching {
            ParcelFileDescriptor.open(
                JSLogging.logFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        }.getOrElse {
            logger.e(it, "getJSLogFD error")
            null
        }
    }

    fun getJSLogPath(): String {
        return JSLogging.logFile.absolutePath
    }

    fun clearJSLogs() {
        JSLogging.clearLogs()
    }

    override fun screenOn() {
        super.screenOn()
        logger.d("onScreenOn")

        handler.postSafety("Call onScreenOn") {
            allTriggers.forEach {
                it.onScreenOn()
            }
        }

        shortXContext.rule.publishFact(
            "onScreenOn",
            ScreenOn.newBuilder().build()
        ) { emptyMap() }
    }

    override fun screenOff() {
        super.screenOff()
        logger.d("screenOff")

        handler.postSafety("Call onScreenOff") {
            allTriggers.forEach {
                it.onScreenOff()
            }
        }

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
        systemServiceScope.launchSafety("notifyInternalFactListeners") {
            val listeners: Array<InternalFactListener> = synchronized(internalFactListeners) {
                internalFactListeners.toTypedArray()
            }
            withContext(Dispatchers.IO) {
                listeners.forEach { it.onFact(fact, initialContextDataLazy) }
            }
        }
    }

    private fun forceDisableShortX() {
        systemServiceScope.launchSafety("forceDisableShortX") {
            setRuleFeatureEnabled(false)
            cancelJobs(getActiveJobs(), "Force disable ShortX")

            // 通知ForceDisable监听器状态已改变
            forceDisableListeners.broadcast {
                kotlin.runCatching { it.onForceDisableStateChanged(true) }
                    .onFailure { logger.e(it, "call onForceDisableStateChanged") }
            }

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

    // SrcId - Proto
    inline fun forEachEnabledFacts(forEachAction: (String, ProtoAny) -> Unit) {
        // Rules
        getAllEnabledRules().forEach { rule ->
            (rule.factsAndQuitFacts).forEach { factProtoAny ->
                forEachAction(rule.id, factProtoAny)
            }
        }

        getAllDirectAction().map { da ->
            da.quitFacts.forEach { factProtoAny ->
                forEachAction(da.id, factProtoAny)
            }

            da.actionsList.forEach { action ->
                if (action is_ WaitUtilConditionMatch::class.java) {
                    val waitUtilConditionMatch = action unpack_ WaitUtilConditionMatch::class.java
                    if (!waitUtilConditionMatch.isDisabled) {
                        val quitFacts = waitUtilConditionMatch.quitFactsList
                        quitFacts.forEach { fact ->
                            forEachAction(da.id, fact)
                        }
                    }
                }
            }
        }
    }

    fun addWebDavProfile(profile: WebDavProfile) {
        webDavProfileStore.addProfile(profile)
    }

    fun removeWebDavProfile(id: String) {
        webDavProfileStore.removeProfile(id)
    }

    fun getAllWebDavProfiles(): List<WebDavProfile> {
        return webDavProfileStore.getAllProfiles()
    }
}