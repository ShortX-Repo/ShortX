package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.DirectActionSettingsStore
import tornaco.apps.shortx.services.data.RuleSettingsStore
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.rule.fact.trigger.allTriggers
import tornaco.apps.shortx.services.systemServiceScope


class RuleService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val ruleSettingsStore: RuleSettingsStore by lazy {
        RuleSettingsStore(systemServiceScope)
    }
    private val dasSettingsStore: DirectActionSettingsStore by lazy {
        DirectActionSettingsStore(systemServiceScope)
    }

    private val allTriggers by lazy {
        allTriggers(context, shortXContext)
    }

    private val ruleChangeListeners = mutableListOf<() -> Unit>()
    private val jobStatusListeners = RemoteCallbackList<IJobStatusListener>()

    private val engine by lazy {
        RuleEngineFactory.newRuleEngine(
            shortXContext = shortXContext,
            androidContext = context,
            handler = handler,
            onJobStarted = { id ->
                jobStatusListeners.broadcast {
                    kotlin.runCatching { it.onJobStarted(id) }
                        .onFailure { logger.e(it, "call onJobStarted") }
                }
            },
            onJobCompleted = { id ->
                jobStatusListeners.broadcast {
                    kotlin.runCatching { it.onJobCompleted(id) }
                        .onFailure { logger.e(it, "call onJobCompleted") }
                }
            },
            onJobCancelled = { id ->
                jobStatusListeners.broadcast {
                    kotlin.runCatching { it.onJobCancelled(id) }
                        .onFailure { logger.e(it, "call onJobCancelled") }
                }
            },
        )
    }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        ruleSettingsStore.readBlocking()
        dasSettingsStore.readBlocking()
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
            val fact = KeyEvent.newBuilder().setKeyCode(it.keyCode).build()
            val enabledRules = getAllRules().filter { rule -> rule.isEnabled }
            if (engine.shouldFire(enabledRules, fact)) {
                if (it.action == android.view.KeyEvent.ACTION_UP) {
                    logger.d("hasAnyRuleMatchFact for keyCode: ${it.keyCode}")
                    engine.fire(fact = fact, rules = enabledRules)
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

    fun publishFact(reason: String, fact: ProtoMessage, contextMap: Map<String, Any> = emptyMap()) {
        logger.d("publishFact: $reason, $fact")
        engine.fire(
            fact = fact,
            rules = getAllRules().filter { rule -> rule.isEnabled },
            contextMap = contextMap
        )
    }

    fun executeActions(id: String, reason: String, actions: List<ProtoAny>) {
        logger.d("executeActions: $reason, $actions $id")
        engine.evaluate(id = id, logTag = reason, actions = actions)
    }

    fun addRule(rule: Rule) {
        ruleSettingsStore.addRule(rule)
        handler.postSafety {
            ruleChanged()
            allTriggers.forEach { it.onAddRule(rule) }
        }
    }

    fun deleteRule(ruleId: String) {
        ruleSettingsStore.deleteRule(ruleId)
        handler.postSafety {
            ruleChanged()
            allTriggers.forEach { it.onDeleteRule() }
        }
    }

    fun getRuleById(id: String): Rule? {
        return ruleSettingsStore.getRuleById(id)
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        logger.d("setRuleEnabled: $ruleId $enable")
        ruleSettingsStore.setRuleEnabled(ruleId, enable)
        handler.postSafety {
            ruleChanged()
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

    fun addRuleChangeListener(listener: () -> Unit) {
        ruleChangeListeners.add(listener)
    }

    fun removeRuleChangeListener(listener: () -> Unit) {
        ruleChangeListeners.remove(listener)
    }

    private fun ruleChanged() {
        ruleChangeListeners.forEach { it() }
    }

    fun addDirectAction(da: DirectAction) {
        dasSettingsStore.addDirectAction(da)
    }

    fun deleteDirectAction(id: String) {
        dasSettingsStore.deleteDirectAction(id)
    }

    fun getDirectActionById(id: String): DirectAction? {
        return dasSettingsStore.getDirectActionById(id)
    }

    fun getAllDirectAction(): List<DirectAction> {
        return dasSettingsStore.getAllDirectActions()
    }

    fun executeDirectionActionById(id: String) {
        getDirectActionById(id)?.let {
            executeActions(id, "executeDirectionActionById#${it.title}", it.actionsList)
        }
    }

    fun registerJobStatusListener(listener: IJobStatusListener) {
        jobStatusListeners.register(listener)
    }

    fun unregisterJobStatusListener(listener: IJobStatusListener) {
        jobStatusListeners.unregister(listener)
    }
}