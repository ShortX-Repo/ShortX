package tornaco.apps.shortx.services.rule

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.condition.TheXXTimeToday
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_Static
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluator
import tornaco.apps.shortx.services.rule.fact.FactMatcher
import tornaco.apps.shortx.services.rule.fact.isMatch
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val evaluateContextSerialNumber = AtomicInteger(2020)
fun nextEvaluateContextSerialNumber() = evaluateContextSerialNumber.getAndIncrement()

const val CONDITION_WAIT_TIMEOUT = 100L

const val LOG_TAG_RULE_QUIT = "Rule-Quit"


interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher
    val coroutineScope: CoroutineScope

    val androidContext: Context
    val shortXContext: ShortXContext

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
        parameterInputs: List<FuncParameterInput> = emptyList(),
        conflictStrategy: ConflictStrategy,
        onComplete: () -> Unit = {}
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
    override val androidContext: Context,
    override val shortXContext: ShortXContext,
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
    ) = factMatcher.match(fact, rule.factsList).isMatch && (
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
    ) = rule.quit.isEnabled && factMatcher.match(fact, rule.quit.factsList).isMatch && (
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
            val factMatcherResult = factMatcher.match(fact, rule.factsList)
            logger.d("factMatcherResult: $factMatcherResult")
            if (factMatcherResult.isMatch) {
                logger.d("fire, rule matched: ${fact.javaClass} ${rule.title}")
                anyMatch = true

                // Prepare initial context data
                val contextData = ContextData.obtain(initialContextDataLazy())
                if (factMatcherResult is FactMatcher.MatcherResult.Match) {
                    contextData.add(
                        GenericContextData.CONTEXT_FACT_TAG,
                        factMatcherResult.factTag
                    )
                }

                // Rule Instance ID
                val ruleInsIdGen: ProtoAny = rule.hasRuleInstanceIdGenerator()
                    .select({ rule.ruleInstanceIdGenerator },
                        {
                            RuleInstanceIdGenerator_Static.newBuilder().setInsId(rule.id).build()
                                .pack_()
                        })
                logger.d("ruleInsIdGen: $ruleInsIdGen")
                val ruleInstanceId = with(RuleInstanceIdGenerator) {
                    ruleInsIdGen.generate(androidContext, shortXContext, contextData)
                } ?: "FALLBACK_${rule.id}"
                logger.d("ruleInstanceId: $ruleInstanceId")


                // **************** Check ConflictStrategy start ******************
                val conflictStrategy: ConflictStrategy = rule.conflictPolicy
                val isThisRuleAlreadyRunning =
                    synchronized(jobs) { jobs.any { it.srcId == rule.id && it.instanceId == ruleInstanceId } }
                if (isThisRuleAlreadyRunning) {
                    synchronized(jobs) {
                        logger.d("isThisRuleAlreadyRunning is true, jobs: ${jobs.joinToString()}")
                    }
                }
                logger.d("${rule.title} conflictStrategy: $conflictStrategy, isThisRuleAlreadyRunning: $isThisRuleAlreadyRunning")
                var shouldExecuteActions = true
                if (isThisRuleAlreadyRunning) {
                    shouldExecuteActions = when (conflictStrategy) {
                        ConflictStrategy.ConflictStrategy_ExecuteBoth, ConflictStrategy.UNRECOGNIZED -> {
                            true
                        }

                        ConflictStrategy.ConflictStrategy_ReplaceOld -> {
                            cancelJobs("ConflictStrategy_ReplaceOld(重复策略)") { it.srcId == rule.id && it.instanceId == ruleInstanceId }
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
                                // Update TheXXTimeToday state.
                                updateTheXXTimeToday(shortXContext, rule.conditionsList)

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

                    logger.d("Created job(ins: $ruleInstanceId): $job for rule: ${rule.title}")
                    synchronized(jobs) {
                        jobs.add(
                            JobRecord(
                                title = rule.title,
                                srcId = rule.id,
                                instanceId = ruleInstanceId,
                                job = job
                            )
                        )
                    }
                    onJobStarted(rule.id)
                    job.invokeOnCompletion {
                        logger.d("job(ins: $ruleInstanceId) for rule: ${rule.title} complete, err: $it")
                        synchronized(jobs) {
                            jobs.remove(
                                JobRecord(
                                    title = rule.title,
                                    srcId = rule.id,
                                    instanceId = ruleInstanceId,
                                    job = job
                                )
                            )
                        }
                        contextData.recycle()
                        onJobCompleted(rule.id)
                    }
                }

            } else {
                logger.d("fire, not match for rule it self: ${fact.javaClass} ${rule.title}, will check it's quit.")

                val quit = rule.quit

                if (quit.isEnabled) {
                    val quitFactMatcherResult = factMatcher.match(fact, quit.factsList)
                    logger.d("quitFactMatcherResult: $quitFactMatcherResult")
                    if (quitFactMatcherResult.isMatch) {
                        logger.d("$LOG_TAG_RULE_QUIT fire, rule-quit matched: ${fact.javaClass} ${rule.title}")
                        anyMatch = true

                        val contextData = ContextData.obtain(initialContextDataLazy())
                        if (quitFactMatcherResult is FactMatcher.MatcherResult.Match) {
                            contextData.add(
                                GenericContextData.CONTEXT_FACT_TAG,
                                quitFactMatcherResult.factTag
                            )
                        }
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId(rule.id)
                            .setSrcEvent(ContextSrcEvent.ContextSrc_RuleQuit)
                            .setSrcTitle(rule.title)
                            .setSerialNumber(nextEvaluateContextSerialNumber())
                            .build()
                        val localVars = LocalVars.obtain()

                        // Rule Instance ID
                        val ruleInsIdGen: ProtoAny = rule.hasRuleInstanceIdGenerator()
                            .select({ rule.ruleInstanceIdGenerator },
                                {
                                    RuleInstanceIdGenerator_Static.newBuilder().setInsId(rule.id)
                                        .build()
                                        .pack_()
                                })
                        logger.d("quit, ruleInsIdGen: $ruleInsIdGen")
                        val ruleInstanceId = with(RuleInstanceIdGenerator) {
                            ruleInsIdGen.generate(androidContext, shortXContext, contextData)
                        } ?: "FALLBACK_${rule.id}"
                        logger.d("quit, ruleInstanceId: $ruleInstanceId")

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
                                        it.srcId == rule.id && it.instanceId == ruleInstanceId
                                    }

                                    updateTheXXTimeToday(shortXContext, quit.conditionsList)

                                    runActionsJob(
                                        ruleTitleForLog = "$LOG_TAG_RULE_QUIT-${rule.title}",
                                        asyncMode = rule.asyncMode,
                                        muteX = rule.muteX,
                                        actionsToRun = quit.actionsList,
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
        parameterInputs: List<FuncParameterInput>,
        conflictStrategy: ConflictStrategy,
        onComplete: () -> Unit
    ) {
        val contextData = ContextData.obtain(initialContextData)
        val isAlreadyRunning = synchronized(jobs) { jobs.any { it.srcId == context.srcId } }
        logger.d("evaluateActions, ${context.srcTitle} conflictStrategy: $conflictStrategy, isAlreadyRunning: $isAlreadyRunning")

        if (isAlreadyRunning) {
            when (conflictStrategy) {
                ConflictStrategy.ConflictStrategy_ExecuteBoth, ConflictStrategy.UNRECOGNIZED -> {
                    // Fall through.
                }

                ConflictStrategy.ConflictStrategy_ReplaceOld -> {
                    cancelJobs("ConflictStrategy_ReplaceOld(重复策略，取消旧任务)") { it.srcId == context.srcId }
                }

                ConflictStrategy.ConflictStrategy_SkipNew -> {
                    logger.d("Skip this rule actions since ConflictStrategy_SkipNew")
                    onComplete()
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
                            localVars = localVars,
                            parameterInputs = parameterInputs
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
            jobs.add(
                JobRecord(
                    title = context.srcTitle,
                    srcId = context.srcId,
                    instanceId = context.srcId,
                    job = job,
                    tags = setOf(context.srcEvent)
                )
            )
        }
        onJobStarted(context.srcId)
        job.invokeOnCompletion {
            logger.d("job for id: ${context.srcId} complete.")
            synchronized(jobs) {
                jobs.removeIf {
                    it.job == job
                }
            }
            contextData.recycle()
            onJobCompleted(context.srcId)
            onComplete()
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
                onJobCancelled(it.srcId)
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

fun updateTheXXTimeToday(shortXContext: ShortXContext, conditions: List<ProtoAny>) {
    val allTheXXTimeTodayConditions =
        conditions.filter { it is_ TheXXTimeToday::class.java }
    allTheXXTimeTodayConditions.forEach {
        val dxtc = it unpack_ TheXXTimeToday::class.java
        if (!dxtc.isInvert) {
            shortXContext.user.didSomethingToday(dxtc.what, dxtc.scope)
        }
    }
}

data class JobRecord(
    val title: String,
    // Rule or DA id.
    val srcId: String,
    val instanceId: String,
    val job: Job,
    val tags: Set<Any> = emptySet()
) {
    fun hasTag(tag: Any) = tags.contains(tag)
}

object JobTags {
}