package tornaco.apps.shortx.services.rule

import android.annotation.TestApi
import android.os.RemoteCallbackList
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.DirectActionSetsStore
import tornaco.apps.shortx.services.data.DirectActionSettingsStore
import tornaco.apps.shortx.services.data.GlobalVarStore
import tornaco.apps.shortx.services.data.RuleSetsStore
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.rule.actor.PrebuiltActor
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteMVEL
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.SingleKeyGestureDetector
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class RuleService(shortXContext: ShortXContext) : SystemService(shortXContext, "RuleService") {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val dasSettingsStore: DirectActionSettingsStore by lazy {
        DirectActionSettingsStore(systemServiceScope)
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

    private val singleKeyGestureDetector: SingleKeyGestureDetector by lazy { SingleKeyGestureDetector.get() }
    private val singleKeyRuleTrigger by lazy {
        SingleKeyRuleTrigger(
            context,
            shortXContext,
            singleKeyGestureDetector
        )
    }

    private val allTriggers by lazy {
        allTriggers(context, shortXContext).apply {
            // These are internal Triggers.
            add(singleKeyRuleTrigger)
        }
    }

    private val jobStatusListeners = RemoteCallbackList<IJobStatusListener>()

    private val globalVarObs = RemoteCallbackList<IGlobalVarObserver>()
    private val ruleObs = RemoteCallbackList<IRuleObserver>()
    private val daObs = RemoteCallbackList<IDAObserver>()

    private val actionEvaluateRecorder = ActionEvaluateRecorder()
    private val conditionEvaluateRecorder = ConditionEvaluateRecorder()
    private val factPublishRecorder = FactPublishRecorder()

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
            actionEvaluateResultListener = { result ->
                logger.d("actionEvaluateResultListener: $result")
                if (isEvaluateRecordEnabled()) {
                    result.toActionEvaluateRecord()?.let { actionEvaluateRecorder.add(it) }
                }
            },
            conditionEvaluateResultListener = { result ->
                logger.d("conditionEvaluateResultListener: $result")
                if (isEvaluateRecordEnabled()) {
                    conditionEvaluateRecorder.add(result.toConditionEvaluateRecord())
                }
            }
        )
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
        globalVarStore.readBlocking()
        ruleSetsStore.readBlocking()
        daSetsStore.readBlocking()

        installListeners()

        PrebuiltActor.values().forEach {
            it.actor.init(context, shortXContext)
        }

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

            // Check if it accessibility service.
            if ((it.flags and android.view.KeyEvent.FLAG_IS_ACCESSIBILITY_EVENT) != 0) {
                return@onKeyEvent null
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
            val enabledRules = getAllEnabledRules()
            if (shouldFire(enabledRules, fact)) {
                if (it.action == android.view.KeyEvent.ACTION_UP) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    handler.postSafety {
                        engine.fire(
                            fact = fact,
                            rules = enabledRules,
                            initialContextData = emptyMap()
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
            val enabledRules = getAllEnabledRules()
            if (shouldFire(enabledRules, fact)) {
                logger.d("hasAnyRuleMatchFact for onGestureEvent: $it")
                handler.postSafety {
                    engine.fire(
                        fact = fact,
                        rules = enabledRules,
                        initialContextData = emptyMap()
                    )
                }
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
    }

    // Quick check.
    fun shouldFire(rules: List<Rule>, fact: ProtoMessage): Boolean {
        return engine.shouldFire(rules, fact)
    }

    fun publishFact(
        reason: String,
        fact: ProtoMessage,
        initialContextData: Map<String, Any> = emptyMap()
    ) {
        val publishTimeMillis = System.currentTimeMillis()
        handler.postSafety {
            logger.i("publishFact, ${fact::class.java}: $reason")
            val anyMatch = engine.fire(
                fact = fact,
                rules = getAllEnabledRules(),
                initialContextData = initialContextData
            )

            if (anyMatch && isEvaluateRecordEnabled()) {
                factPublishRecorder.add(fact.toFactPublishRecord(publishTimeMillis))
            }
        }
    }

    fun executeActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars = LocalVars.obtain(),
        conflictStrategy: ConflictStrategy,
    ) {
        logger.d("executeActions: ${context.srcEvent} ${context.srcTitle}")
        engine.evaluateActions(
            context = context,
            actions = actions,
            initialContextData = initialContextData,
            localVars = localVars,
            conflictStrategy = conflictStrategy
        )
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
        val token = extras.firstOrNull { it.first == BuildProp.APP_PACKAGE_NAME }
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
            logger.p("Can not add rule.")
            return
        }

        // Must clear rule extras!
        val rule = Rule.newBuilder(unMaskedRule).clearExtras().build()

        val existing = getRuleById(rule.id)
        if (existing == null) {
            // Add
            ruleSettingsStore.addRule(rule)
            handler.postSafety {
                allTriggers.forEach { it.onAddRule(rule) }
            }
        } else {
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
    }

    fun getRuleById(id: String): Rule? {
        return ruleSettingsStore.getRuleById(id)
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        logger.d("setRuleEnabled: $ruleId $enable")
        ruleSettingsStore.setRuleEnabled(ruleId, enable)
        handler.postSafety {
            allTriggers.forEach {
                if (enable) it.onRuleEnabled(ruleId) else it.onRuleDisabled(ruleId)
            }
        }
    }

    fun getActiveJobs(): List<String> = engine.activeJobs().map { it.id }

    fun cancelJobs(jobs: List<String>) {
        handler.postSafety {
            engine.cancelJobs("User requested.") {
                jobs.contains(it.id)
            }
        }
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }

    fun getAllEnabledRules() = getAllRules().filter { it.isEnabled }

    fun getRuleCount() = ruleSettingsStore.getRuleCount()

    fun addDirectAction(unMaskedDA: DirectAction) {
        // Check Subscribe state.
        val extras = unMaskedDA.extrasList
        val token = extras.firstOrNull { it.first == BuildProp.APP_PACKAGE_NAME }
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
            logger.p("Can not add DA.")
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
    }

    fun deleteDirectAction(id: String) {
        dasSettingsStore.deleteDirectAction(id)
        daObs.broadcast {
            kotlin.runCatching { it.onDelete(id) }
                .onFailure { logger.e("Call onDelete") }
        }
    }

    fun getDirectActionById(id: String): DirectAction? {
        return dasSettingsStore.getDirectActionById(id)
    }

    fun getAllDirectAction(): List<DirectAction> {
        return dasSettingsStore.getAllDirectActions()
    }

    fun getDirectActionCount() = dasSettingsStore.getDirectActionCount()

    fun executeDirectionActionById(context: EvaluateContext, id: String) {
        getDirectActionById(id)?.let {
            executeActions(
                context = context,
                actions = it.actionsList,
                conflictStrategy = it.conflictPolicy
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
            conflictStrategy = directAction.conflictPolicy
        )
    }

    fun directExecuteRuleActions(context: EvaluateContext, ruleId: String) {
        val rule = getRuleById(ruleId)
        logger.d("directExecuteRuleActions: $ruleId ${rule?.title}")
        rule?.let {
            val actions = it.actionsList
            executeActions(
                context = context,
                actions = actions,
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth
            )
        }
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

    fun isEvaluateRecordEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableEvaluateRecorder)
    }

    fun setEvaluateRecordEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableEvaluateRecorder, enable)
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
        store.write(Settings.BoolSettings.EnableRuleEngine(engine), enable)
    }

    fun isRuleEngineEnabled(engine: String): Boolean {
        return store.read(Settings.BoolSettings.EnableRuleEngine(engine))
    }
}