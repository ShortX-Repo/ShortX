package tornaco.apps.shortx.services.rule

import android.annotation.TestApi
import android.os.RemoteCallbackList
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.context.ContextDataMapping
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
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.DirectActionSettingsStore
import tornaco.apps.shortx.services.data.GlobalVarStore
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteMVEL
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.systemServiceScope
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
            actionEvaluateResultListener = { result, context ->
                logger.d("actionEvaluateResultListener: $result")
                if (isActionEvaluateRecorderEnabled()) {
                    result.toActionEvaluateRecord(context)?.let { actionEvaluateRecorder.add(it) }
                }
            },
            conditionEvaluateResultListener = {
                logger.d("conditionEvaluateResultListener: $it")
                if (isConditionEvaluateRecorderEnabled()) {
                    conditionEvaluateRecorder.add(it.toConditionEvaluateRecord())
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
        installListeners()
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
            if (singleKeyRuleTrigger.hasAny(it.keyCode)) {
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

        allTriggers.forEach {
            kotlin.runCatching {
                logger.d("Register trigger: $it")
                it.register()
            }.onFailure { logger.e("Fail register trigger $it") }
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
        handler.postSafety {
            logger.i("publishFact, ${fact::class.java}: $reason")
            val anyMatch = engine.fire(
                fact = fact,
                rules = getAllEnabledRules(),
                initialContextData = initialContextData
            )

            if (anyMatch && isFactPublishRecordEnabled()) {
                factPublishRecorder.add(fact.toFactPublishRecord())
            }
        }
    }

    fun executeActions(
        id: String,
        reason: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap(),
        conflictStrategy: ConflictStrategy
    ) {
        logger.d("executeActions: $reason, $actions $id")
        engine.evaluateActions(
            containerId = id,
            logTag = reason,
            actions = actions,
            initialContextData = initialContextData,
            conflictStrategy = conflictStrategy
        )
    }

    @TestApi
    fun executeActionsBlocking(
        id: String,
        reason: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap()
    ) {
        val latch = CountDownLatch(1)
        val listener = object : IJobStatusListener.Stub() {
            override fun onJobStarted(id: String?) {
            }

            override fun onJobCompleted(completion: String?) {
                if (id == completion) {
                    latch.countDown()
                }
            }

            override fun onJobCancelled(id: String?) {
            }
        }
        registerJobStatusListener(listener)
        executeActions(
            id,
            reason,
            actions,
            initialContextData,
            ConflictStrategy.ConflictStrategy_ExecuteBoth
        )
        latch.await(60, TimeUnit.SECONDS)
        unregisterJobStatusListener(listener)
    }

    fun addOrUpdateRule(rule: Rule) {
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
        engine.cancelJobs("User requested.") {
            jobs.contains(it.id)
        }
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }

    fun getAllEnabledRules() = getAllRules().filter { it.isEnabled }

    fun getRuleCount() = ruleSettingsStore.getRuleCount()

    fun addDirectAction(da: DirectAction) {
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

    fun executeDirectionActionById(id: String) {
        getDirectActionById(id)?.let {
            executeActions(
                id = id,
                reason = "executeDirectionActionById#${it.title}",
                actions = it.actionsList,
                initialContextData = mapOf(
                    ContextDataMapping.Context.contextSrcTitle.name to it.title,
                    ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_DirectAction,
                ),
                it.conflictPolicy
            )
        }
    }

    fun executeDirectionAction(directAction: DirectAction) {
        executeActions(
            id = directAction.id,
            reason = "executeDirectionAction#${directAction.title}",
            actions = directAction.actionsList,
            initialContextData = mapOf(
                ContextDataMapping.Context.contextSrcTitle.name to directAction.title,
                ContextDataMapping.Context.contextSrcId.name to directAction.id,
                ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_ExecuteOnDAEditor,
            ),
            directAction.conflictPolicy
        )
    }

    fun directExecuteRuleActions(ruleId: String) {
        val rule = getRuleById(ruleId)
        logger.d("directExecuteRuleActions: $ruleId ${rule?.title}")
        rule?.let {
            val actions = it.actionsList
            executeActions(
                id = ruleId,
                reason = "directExecuteRuleActions",
                actions = actions,
                initialContextData = mapOf(
                    ContextDataMapping.Context.contextSrcTitle.name to "Direct execute: ${rule.title}",
                    ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_ExecuteRuleActions,
                ),
                ConflictStrategy.ConflictStrategy_ExecuteBoth
            )
        }
    }

    fun executeMVEL(expression: String): String {
        var res = ""
        runBlocking {
            systemServiceScope.launchSafety {
                val handleParams = dummyHandlerParamsForAction(
                    context,
                    shortXContext,
                    tornaco.apps.shortx.core.proto.action.ExecuteMVEL.newBuilder()
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

    fun evaluateCondition(
        condition: ProtoAny,
    ): Boolean {
        return engine.evaluateConditions(
            "RuleService evaluateCondition",
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

    fun setActionEvaluateRecorderEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableActionEvaluateRecorder, enable)
    }

    fun isActionEvaluateRecorderEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableActionEvaluateRecorder)
    }

    fun getConditionEvaluateRecords(): List<ConditionEvaluateRecord> {
        return conditionEvaluateRecorder.conditionEvaluateRecords
    }

    fun clearConditionEvaluateRecords() {
        conditionEvaluateRecorder.clear()
    }

    fun setConditionEvaluateRecorderEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableConditionEvaluateRecorder, enable)
    }

    fun isConditionEvaluateRecorderEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableConditionEvaluateRecorder)
    }

    fun getFactPublishRecords(): List<FactPublishRecord> {
        return factPublishRecorder.records
    }

    fun clearFactPublishRecords() {
        factPublishRecorder.clear()
    }

    fun setFactPublishRecordEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableFactPublishRecorder, enable)
    }

    fun isFactPublishRecordEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableFactPublishRecorder)
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
}