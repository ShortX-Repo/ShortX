package tornaco.apps.shortx.services.rule

import android.annotation.TestApi
import android.content.Context
import android.os.RemoteCallbackList
import android.widget.Toast
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
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
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.SingleKeyGestureDetector
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class RuleService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val dasSettingsStore: DirectActionSettingsStore by lazy {
        DirectActionSettingsStore(systemServiceScope)
    }

    private val globalVarStore: GlobalVarStore by lazy {
        GlobalVarStore(
            scope = systemServiceScope,
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
            actionEvaluateResultListener = {
                logger.d("actionEvaluateResultListener: $it")
                if (isActionEvaluateRecorderEnabled()) {
                    actionEvaluateRecorder.add(it.toActionEvaluateRecord())
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

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
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
            val enabledRules = getAllRules().filter { rule -> rule.isEnabled }
            if (engine.shouldFire(enabledRules, fact)) {
                if (it.action == android.view.KeyEvent.ACTION_UP) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    engine.fire(
                        fact = fact,
                        rules = enabledRules
                    )
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
            if (shouldFire(enabledRules, fact)) {
                logger.d("hasAnyRuleMatchFact for onGestureEvent: $it")
                engine.fire(fact = fact, rules = enabledRules)
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
        logger.i("publishFact, ${fact::class.java}: $reason")
        val anyMatch = engine.fire(
            fact = fact,
            rules = getAllRules().filter { rule -> rule.isEnabled },
            initialContextData = initialContextData
        )

        if (anyMatch && isFactPublishRecordEnabled()) {
            factPublishRecorder.add(fact.toFactPublishRecord())
        }
    }

    fun executeActions(
        id: String,
        reason: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap()
    ) {
        logger.d("executeActions: $reason, $actions $id")
        engine.evaluate(
            id = id,
            logTag = reason,
            actions = actions,
            initialContextData = initialContextData
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
        executeActions(id, reason, actions, initialContextData)
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
        engine.cancelJobs {
            jobs.contains(it.id)
        }
    }

    fun getAllRules(): List<Rule> {
        return ruleSettingsStore.getAllRules()
    }

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
            executeActions(id, "executeDirectionActionById#${it.title}", it.actionsList)
        }
    }

    fun executeDirectionAction(directAction: DirectAction) {
        executeActions(
            directAction.id,
            "executeDirectionAction#${directAction.title}",
            directAction.actionsList
        )
    }

    fun directExecuteRuleActions(ruleId: String) {
        val rule = getRuleById(ruleId)
        logger.d("directExecuteRuleActions: $ruleId ${rule?.title}")
        rule?.let {
            val actions = it.actionsList
            executeActions(ruleId, "directExecuteRuleActions", actions)
        }
    }

    fun executeMVEL(expression: String): String {
        return kotlin.runCatching {
            "${MVELExecutor(context, shortXContext, expression).invoke()}"
        }.getOrElse {
            logger.e(it, "executeMVEL fail")
            it.message ?: it.javaClass.name
        }
    }

    fun evaluateCondition(
        condition: ProtoAny,
    ): Boolean {
        return engine.evaluate(
            "RuleService evaluateCondition",
            condOp = ConditionOperator.ALL,
            condOpPayload = ConditionOperatorPayload.newBuilder().build(),
            conditions = listOf(condition),
            contextData = ContextData.obtain()
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