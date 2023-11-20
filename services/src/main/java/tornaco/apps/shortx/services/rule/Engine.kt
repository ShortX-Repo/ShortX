package tornaco.apps.shortx.services.rule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val evaluateContextSerialNumber = AtomicInteger(2020)
fun nextEvaluateContextSerialNumber() = evaluateContextSerialNumber.getAndIncrement()

const val CONDITION_WAIT_TIMEOUT = 100L

const val LOG_TAG_RULE_QUIT = "Rule-Quit"

interface FactMatcher {
    fun match(actualFact: ProtoMessage, ruleFacts: List<ProtoAny>): Boolean
}

interface ConditionEvaluator {
    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condition: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars
    ): EvaluateResult

    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData,
        localVars: LocalVars
    ): Boolean

    sealed interface EvaluateResult {
        val evaluateContext: EvaluateContext

        data class Success(
            val condition: ProtoAny,
            val match: Boolean,
            val reason: String,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Fail(
            val condition: ProtoAny,
            val error: Throwable,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Break(override val evaluateContext: EvaluateContext) : EvaluateResult
    }
}

fun ConditionEvaluator.EvaluateResult.isMatch() =
    this is ConditionEvaluator.EvaluateResult.Success && this.match

interface ActionEvaluator {
    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        action: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars
    ): EvaluateResult

    sealed interface EvaluateResult {
        val evaluateContext: EvaluateContext
        val actionId: String

        data class Success(
            override val actionId: String,
            val action: ProtoAny,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Fail(
            val shouldContinue: Boolean,
            override val actionId: String,
            val action: ProtoAny,
            val error: Throwable,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Break(
            override val actionId: String,
            val action: ProtoAny,
            val scope: BreakActionExecuteScope,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Disabled(
            override val actionId: String,
            val action: ProtoAny,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult
    }
}

class BreakActionException(override val message: String, val scope: BreakActionExecuteScope) :
    RuntimeException()

interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher
    val coroutineScope: CoroutineScope

    fun shouldFire(
        rules: List<Rule>,
        fact: ProtoMessage,
        // For normal fact, no need to check condition
        // For KeyEvent, pls check condition before intercept system
        checkCondition: Boolean
    ): Boolean

    fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        initialContextDataLazy: () -> Map<String, Any>
    ): Boolean

    fun evaluateActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any>,
        localVars: LocalVars = LocalVars.obtain(),
        conflictStrategy: ConflictStrategy
    )

    fun evaluateConditions(
        context: EvaluateContext,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        initialContextData: Map<String, Any>,
    ): Boolean

    fun cancelJobs(reason: String, filter: (JobRecord) -> Boolean)

    fun activeJobs(): List<JobRecord>
}

class DefaultRuleEngine(
    override val coroutineScope: CoroutineScope,
    override val factMatcher: FactMatcher,
    override val conditionEvaluator: ConditionEvaluator,
    override val actionEvaluator: ActionEvaluator,
    private val onJobStarted: (String) -> Unit,
    private val onJobCompleted: (String) -> Unit,
    private val onJobCancelled: (String) -> Unit,
    private val isRuleFeatureEnabled: () -> Boolean,
    private val isSystemReadyPublished: () -> Boolean,
) : Engine {
    private val logger = Logger("DefaultRuleEngine")
    private val jobs = mutableListOf<JobRecord>()

    private val globalMuteX = Mutex()
    private val muteXMap = ConcurrentHashMap<String, Mutex>()

    private val defaultCondRes: (Rule) -> Boolean = {
        logger.w("shouldFire, check condition timeout, return false. rule is: ${it.title}")
        false
    }

    private val Rule.muteX get() = muteXMap.computeIfAbsent(this.id) { Mutex() }

    private val preCheckContext = EvaluateContext.newBuilder()
        .setSrcId("PreCheck")
        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
        .setSrcTitle("PreCheck")
        .setSerialNumber(nextEvaluateContextSerialNumber())
        .build()

    override fun shouldFire(
        rules: List<Rule>,
        fact: ProtoMessage,
        checkCondition: Boolean
    ): Boolean {
        if (!isRuleFeatureEnabled()) return false
        val startTimeMillis = System.currentTimeMillis()
        return rules.filterInvalid().any {
            shouldFireRuleForFact(
                context = preCheckContext,
                fact = fact,
                rule = it,
                checkCondition = checkCondition
            ) || shouldFireRuleZQuitForFact(
                context = preCheckContext,
                fact = fact,
                rule = it,
                checkCondition = checkCondition
            )
        }.also {
            logger.d("shouldFire check taken: ${System.currentTimeMillis() - startTimeMillis}ms")
        }
    }

    private fun shouldFireRuleForFact(
        context: EvaluateContext,
        fact: ProtoMessage,
        rule: Rule,
        checkCondition: Boolean
    ) = factMatcher.match(fact, rule.factsList) && (
            !checkCondition || (
                    runBlocking {
                        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
                            conditionEvaluator.evaluate(
                                evaluateContext = context,
                                condOp = rule.condOp,
                                condOpPayload = rule.condOpPayload,
                                conditions = rule.conditionsList,
                                contextData = ContextData.obtain(),
                                localVars = LocalVars.obtain()
                            )
                        }
                    } ?: defaultCondRes(rule))
            )

    private fun shouldFireRuleZQuitForFact(
        context: EvaluateContext,
        fact: ProtoMessage,
        rule: Rule,
        checkCondition: Boolean
    ) = rule.quit.isEnabled && factMatcher.match(fact, rule.quit.factsList) && (
            !checkCondition || (
                    runBlocking {
                        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
                            conditionEvaluator.evaluate(
                                evaluateContext = context,
                                condOp = rule.quit.condOp,
                                condOpPayload = rule.quit.condOpPayload,
                                conditions = rule.quit.conditionsList,
                                contextData = ContextData.obtain(),
                                localVars = LocalVars.obtain()
                            )
                        }
                    } ?: defaultCondRes(rule))
            )

    // Should call in Handler to make it Thread safe.
    override fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        initialContextDataLazy: () -> Map<String, Any>
    ): Boolean {
        if (!isRuleFeatureEnabled()) {
            logger.w("Drop fact since Rule feature is disabled: $fact")
            return false
        }
        if (fact is SystemReady) {
            logger.w("fire SystemReady")
        }

        if (fact !is SystemReady && !isSystemReadyPublished()) {
            logger.w("Drop fact since System NOT ready: $fact")
            return false
        }

        logger.d("fire: ${fact.javaClass}")
        var anyMatch = false
        rules.filterInvalid().forEach { rule ->
            // Try fire for Rule.
            if (factMatcher.match(fact, rule.factsList)) {
                logger.d("fire, rule matched: ${fact.javaClass} ${rule.title}")
                anyMatch = true

                // **************** Check ConflictStrategy start ******************
                val conflictStrategy: ConflictStrategy = rule.conflictPolicy
                val isThisRuleAlreadyRunning = synchronized(jobs) { jobs.any { it.id == rule.id } }
                logger.d("${rule.title} conflictStrategy: $conflictStrategy, isThisRuleAlreadyRunning: $isThisRuleAlreadyRunning")
                var shouldExecuteActions = true
                if (isThisRuleAlreadyRunning) {
                    shouldExecuteActions = when (conflictStrategy) {
                        ConflictStrategy.ConflictStrategy_ExecuteBoth, ConflictStrategy.UNRECOGNIZED -> {
                            true
                        }

                        ConflictStrategy.ConflictStrategy_ReplaceOld -> {
                            cancelJobs("ConflictStrategy_ReplaceOld(重复策略)") { it.id == rule.id }
                            true
                        }

                        ConflictStrategy.ConflictStrategy_SkipNew -> {
                            logger.d("Skip this rule actions since ConflictStrategy_SkipNew")
                            false
                        }
                    }
                }
                logger.d("shouldExecuteActions for rule ${rule.title} $shouldExecuteActions")
                // ****************** Check ConflictStrategy end ***************************

                if (shouldExecuteActions) {
                    // Init data
                    val contextData = ContextData.obtain(initialContextDataLazy())
                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(rule.id)
                        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                        .setSrcTitle(rule.title)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build()

                    val job = coroutineScope.launchSafety {
                        // Launch Start
                        withContext(Dispatchers.IO) {
                            // Match condition
                            val conditionRes: Boolean = if (rule.conditionsList.isEmpty()) {
                                logger.d("this rule: ${rule.title} has no condition, assume match!")
                                true
                            } else {
                                conditionEvaluator.evaluate(
                                    evaluateContext = evaluateContext,
                                    condOp = rule.condOp,
                                    condOpPayload = rule.condOpPayload,
                                    conditions = rule.conditionsList,
                                    contextData = contextData,
                                    // No local var for a Rule condition.
                                    localVars = LocalVars.obtain()
                                )
                            }

                            logger.d("conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes")

                            if (conditionRes) {
                                val localVars = LocalVars.obtain()
                                runActionsJob(
                                    ruleTitleForLog = rule.title,
                                    asyncMode = rule.asyncMode,
                                    muteX = rule.muteX,
                                    actionsToRun = rule.actionsList,
                                    evaluateContext = evaluateContext,
                                    contextData = contextData,
                                    localVars = localVars
                                )
                            } else {
                                logger.d("Won't runActions since condition not match.")
                            }
                        }
                        // Launch END.
                    }

                    logger.d("Created job: $job for rule: ${rule.title}")
                    synchronized(jobs) {
                        jobs.add(JobRecord(rule.id, job))
                    }
                    onJobStarted(rule.id)
                    job.invokeOnCompletion {
                        logger.d("job for rule: ${rule.title} complete, err: $it")
                        synchronized(jobs) {
                            jobs.remove(JobRecord(rule.id, job))
                        }
                        contextData.recycle()
                        onJobCompleted(rule.id)
                    }
                }

            } else {
                logger.d("fire, not match for rule it self: ${fact.javaClass} ${rule.title}, will check it's quit.")

                val quit = rule.quit
                if (quit.isEnabled && factMatcher.match(fact, quit.factsList)) {
                    logger.d("$LOG_TAG_RULE_QUIT fire, rule-quit matched: ${fact.javaClass} ${rule.title}")
                    anyMatch = true

                    val contextData = ContextData.obtain(initialContextDataLazy())
                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(rule.id)
                        .setSrcEvent(ContextSrcEvent.ContextSrc_RuleQuit)
                        .setSrcTitle(rule.title)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build()
                    val localVars = LocalVars.obtain()

                    coroutineScope.launchSafety {
                        // Launch start
                        withContext(Dispatchers.IO) {
                            val conditionRes = if (quit.conditionsList.isEmpty()) {
                                logger.d("$LOG_TAG_RULE_QUIT quit for rule: ${rule.title} has no condition, assume match!")
                                true
                            } else {
                                conditionEvaluator.evaluate(
                                    evaluateContext = evaluateContext,
                                    condOp = quit.condOp,
                                    condOpPayload = quit.condOpPayload,
                                    conditions = quit.conditionsList,
                                    contextData = contextData,
                                    // No local var for a Rule condition.
                                    localVars = LocalVars.obtain()
                                )
                            }
                            logger.d("$LOG_TAG_RULE_QUIT conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes")
                            if (conditionRes) {
                                logger.d("$LOG_TAG_RULE_QUIT about to quit ${rule.title}")

                                // DO NOT cancel ourself...
                                cancelJobs("Rule quit(退出指令)") {
                                    it.id == rule.id
                                }

                                runActionsJob(
                                    ruleTitleForLog = "$LOG_TAG_RULE_QUIT-${rule.title}",
                                    asyncMode = rule.asyncMode,
                                    muteX = rule.muteX,
                                    actionsToRun = rule.quit.actionsList,
                                    evaluateContext = evaluateContext,
                                    contextData = contextData,
                                    localVars = localVars
                                )
                            }
                            // Launch End
                        }
                    }
                }
            }
        }
        return anyMatch
    }

    private suspend fun runActionsJob(
        ruleTitleForLog: String,
        asyncMode: AsyncMode,
        muteX: Mutex,
        actionsToRun: List<ProtoAny>,
        evaluateContext: EvaluateContext,
        contextData: ContextData,
        localVars: LocalVars
    ) {
        // By default, execute action in IO
        logger.d("job for rule: $ruleTitleForLog start.")
        val runActions: suspend () -> Unit = {
            kotlin.run loop@{
                logger.d("about to execute ${actionsToRun.size} actions for $ruleTitleForLog")
                actionsToRun.forEach {
                    logger.d("about to execute action: ${it.typeUrl}")
                    val evaRes = actionEvaluator.evaluate(
                        evaluateContext = evaluateContext,
                        action = it,
                        contextData = contextData,
                        localVars = localVars,
                    )
                    logger.d("action evaluate res: $evaRes")
                    if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                        logger.e(evaRes.error, "evaluate fail")
                        if (evaRes.shouldContinue) {
                            logger.d("Action on error is continue execute.")
                        } else {
                            return@loop
                        }
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                        logger.w("evaluate break!")
                        return@loop
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Disabled) {
                        logger.d("action is disabled: ${evaRes.action.typeUrl}")
                    }
                }
            }
        }

        when (asyncMode) {
            AsyncMode.AsyncMode_Async, AsyncMode.UNRECOGNIZED -> {
                logger.d("Run actions with Async")
                runActions()
            }

            AsyncMode.AsyncMode_Sync_InRule -> {
                logger.d("Run actions with Sync_InRule: $muteX")
                muteX.withLock {
                    runActions()
                }
            }

            else -> {
                logger.d("Run actions with globalMuteX")
                globalMuteX.withLock {
                    runActions()
                }
            }
        }
    }

    override fun evaluateActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any>,
        localVars: LocalVars,
        conflictStrategy: ConflictStrategy
    ) {
        val contextData = ContextData.obtain(initialContextData)
        val isAlreadyRunning = synchronized(jobs) { jobs.any { it.id == context.srcId } }
        logger.d("evaluateActions, ${context.srcTitle} conflictStrategy: $conflictStrategy, isAlreadyRunning: $isAlreadyRunning")

        if (isAlreadyRunning) {
            when (conflictStrategy) {
                ConflictStrategy.ConflictStrategy_ExecuteBoth, ConflictStrategy.UNRECOGNIZED -> {
                    // Fall through.
                }

                ConflictStrategy.ConflictStrategy_ReplaceOld -> {
                    cancelJobs("ConflictStrategy_ReplaceOld(重复策略，取消旧任务)") { it.id == context.srcId }
                }

                ConflictStrategy.ConflictStrategy_SkipNew -> {
                    logger.d("Skip this rule actions since ConflictStrategy_SkipNew")
                    return
                }
            }
        }

        val job = coroutineScope.launchSafety {
            withContext(Dispatchers.IO) {
                logger.d("evaluateActions, job for id: ${context.srcId} start.")
                kotlin.run loop@{
                    actions.forEach {
                        val evaRes = actionEvaluator.evaluate(
                            evaluateContext = context,
                            action = it,
                            contextData = contextData,
                            localVars = localVars
                        )
                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                            logger.e(evaRes.error, "evaluate fail")
                            if (evaRes.shouldContinue) {
                                logger.d("Action on error is continue execute.")
                            } else {
                                return@loop
                            }
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                            logger.w("evaluate break!")
                            return@loop
                        } else if (evaRes is ActionEvaluator.EvaluateResult.Disabled) {
                            logger.d("action is disabled: ${evaRes.action.typeUrl}")
                        }
                    }
                }
            }
        }
        synchronized(jobs) {
            // Add tag to identify if this is from RuleHook, it should not be canceled on Rule.Disable
            jobs.add(JobRecord(context.srcId, job, tags = setOf(context.srcEvent)))
        }
        onJobStarted(context.srcId)
        job.invokeOnCompletion {
            logger.d("job for id: ${context.srcId} complete.")
            synchronized(jobs) {
                jobs.remove(JobRecord(context.srcId, job))
            }
            contextData.recycle()
            onJobCompleted(context.srcId)
        }
    }

    // This API is exposed for App check condition on Edit.
    // So let is run blocking.
    override fun evaluateConditions(
        context: EvaluateContext,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        initialContextData: Map<String, Any>,
    ): Boolean {
        return runBlocking {
            conditionEvaluator.evaluate(
                context,
                condOp,
                condOpPayload,
                conditions,
                ContextData.obtain(initialContextData),
                localVars = LocalVars.obtain()
            )
        }
    }

    override fun cancelJobs(reason: String, filter: (JobRecord) -> Boolean) {
        synchronized(jobs) {
            val canceled = jobs.filter(filter).onEach {
                logger.d("cancel job: $it, reason: $reason")
                it.job.cancel(CancellationException(reason))
                logger.d("After call cancel, job isCanceled: ${it.job.isCancelled}, job isActive: ${it.job.isActive}")
                onJobCancelled(it.id)
            }
            jobs.removeAll(canceled)
        }
    }

    override fun activeJobs(): List<JobRecord> {
        return synchronized(jobs) {
            jobs.filter { it.job.isActive }
        }
    }
}

private fun List<Rule>.filterInvalid() =
    filter { it.actionsList.isNotEmpty() || it.quit.actionsList.isNotEmpty() }

data class JobRecord(val id: String, val job: Job, val tags: Set<Any> = emptySet()) {
    fun hasTag(tag: Any) = tags.contains(tag)
}

object JobTags {
}