package tornaco.apps.shortx.services.rule

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.condition.TheXXTimeToday
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Quit_EvaluatePolicy
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_Static
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.asDefaultInput
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

    // canEvaluateQuitDAInsWhenQuitPolicyIsEvaluateOnlyOnce
    private val quitEvaPolicyOO = mutableSetOf<String>()
    private fun <T> withQuitEvaPolicyOO(action: MutableSet<String>.() -> T): T {
        return synchronized(quitEvaPolicyOO) {
            action.invoke(quitEvaPolicyOO)
        }
    }

    private val globalMuteX = Mutex()
    private val muteXMap = ConcurrentHashMap<String, Mutex>()

    private val defaultCondRes: (Rule) -> Boolean = {
        logger.w("shouldFire, check condition timeout, return false. rule is: ${it.title}")
        false
    }
    private val defaultDACondRes: (DirectAction) -> Boolean = {
        logger.w("shouldFire, check condition timeout, return false. da is: ${it.title}")
        false
    }

    private val Rule.muteX get() = muteXMap.computeIfAbsent(this.id) { Mutex() }
    private val DirectAction.muteX get() = muteXMap.computeIfAbsent(this.id) { Mutex() }

    private val preCheckContext = EvaluateContext.newBuilder()
        .setSrcId("PreCheck")
        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
        .setSrcTitle("PreCheck")
        .setSerialNumber(nextEvaluateContextSerialNumber())
        .build()

    override fun shouldFire(
        rules: List<Rule>,
        das: List<DirectAction>,
        fact: ProtoMessage,
        checkCondition: Boolean,
        excludeFacts: Set<String>
    ): Boolean {
        if (!isRuleFeatureEnabled()) return false
        val startTimeMillis = System.currentTimeMillis()
        return rules.filterInvalid().any { rule ->
            shouldFireRuleForFact(
                context = preCheckContext,
                fact = fact,
                rule = rule,
                checkCondition = checkCondition,
                excludeFacts = excludeFacts
            ).also { logger.d("shouldFireRuleForFact: $fact ${rule.title} $it") } || shouldFireRuleZQuitForFact(
                context = preCheckContext,
                fact = fact,
                rule = rule,
                checkCondition = checkCondition,
                excludeFacts = excludeFacts
            ).also { logger.d("shouldFireRuleZQuitForFact: $fact ${rule.title} $it") }
        } || das.any { da ->
            shouldFireDAZQuitForFact(
                context = preCheckContext,
                fact = fact,
                da = da,
                checkCondition = checkCondition,
                excludeFacts = excludeFacts
            ).also { logger.d("shouldFireDAZQuitForFact: $fact ${da.title} $it") }
        }.also {
            logger.logOnDebug { ("shouldFire check taken: ${System.currentTimeMillis() - startTimeMillis}ms") }
        }
    }

    private fun shouldFireRuleForFact(
        context: EvaluateContext,
        fact: ProtoMessage,
        rule: Rule,
        checkCondition: Boolean,
        excludeFacts: Set<String>
    ) = factMatcher.match(fact, rule.factsList.filterNot {
        excludeFacts.contains(it.typeUrl)
    }).isMatch && (
            !checkCondition || (
                    runBlocking {
                        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
                            conditionEvaluator.evaluate(
                                evaluateContext = context,
                                condOp = rule.condOp,
                                condOpPayload = rule.condOpPayload,
                                conditions = rule.conditionsList,
                                contextData = ContextData.obtain(),
                                localVars = LocalVars.obtain(),
                                parameterInputs = emptyList()
                            )
                        }
                    } ?: defaultCondRes(rule))
            )

    private fun shouldFireRuleZQuitForFact(
        context: EvaluateContext,
        fact: ProtoMessage,
        rule: Rule,
        checkCondition: Boolean,
        excludeFacts: Set<String>
    ) = rule.quit.isEnabled && factMatcher.match(fact, rule.quit.factsList.filterNot {
        excludeFacts.contains(it.typeUrl)
    }).isMatch && (
            !checkCondition || (
                    runBlocking {
                        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
                            conditionEvaluator.evaluate(
                                evaluateContext = context,
                                condOp = rule.quit.condOp,
                                condOpPayload = rule.quit.condOpPayload,
                                conditions = rule.quit.conditionsList,
                                contextData = ContextData.obtain(),
                                localVars = LocalVars.obtain(),
                                parameterInputs = emptyList()
                            )
                        }
                    } ?: defaultCondRes(rule))
            )

    private fun shouldFireDAZQuitForFact(
        context: EvaluateContext,
        fact: ProtoMessage,
        da: DirectAction,
        checkCondition: Boolean,
        excludeFacts: Set<String>
    ) = da.quit.isEnabled && factMatcher.match(fact, da.quit.factsList.filterNot {
        excludeFacts.contains(it.typeUrl)
    }).isMatch && (
            !checkCondition || (
                    runBlocking {
                        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
                            conditionEvaluator.evaluate(
                                evaluateContext = context,
                                condOp = da.quit.condOp,
                                condOpPayload = da.quit.condOpPayload,
                                conditions = da.quit.conditionsList,
                                contextData = ContextData.obtain(),
                                localVars = LocalVars.obtain(),
                                parameterInputs = emptyList()
                            )
                        }
                    } ?: defaultDACondRes(da))
            )

    // Should call in Handler to make it Thread safe.
    override fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        das: List<DirectAction>,
        initialContextDataLazy: () -> Map<String, Any>
    ): Boolean {
        if (!isRuleFeatureEnabled()) {
            logger.w("Drop fact since Rule feature is disabled: ${fact.javaClass}")
            return false
        }
        if (fact is SystemReady) {
            logger.w("fire SystemReady")
        }

        if (fact !is SystemReady && !isSystemReadyPublished()) {
            logger.w("Drop fact since System NOT ready: ${fact.javaClass}")
            return false
        }

        logger.logOnDebug { ("fire: ${fact.javaClass}") }
        var anyMatch = false
        rules.filterInvalid().forEach { rule ->
            // Try fire for Rule.
            val factMatcherResult = factMatcher.match(fact, rule.factsList)
            logger.logOnDebug { ("factMatcherResult: $factMatcherResult") }
            if (factMatcherResult.isMatch) {
                logger.logOnDebug { ("fire, rule matched: ${fact.javaClass} ${rule.title}") }
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
                    .select(
                        { rule.ruleInstanceIdGenerator },
                        {
                            RuleInstanceIdGenerator_Static.newBuilder().setInsId(rule.id).build()
                                .pack_()
                        })
                logger.logOnDebug { ("ruleInsIdGen: $ruleInsIdGen") }
                val ruleInstanceId = with(RuleInstanceIdGenerator) {
                    ruleInsIdGen.generate(androidContext, shortXContext, contextData)
                } ?: "FALLBACK_${rule.id}"
                logger.logOnDebug { ("ruleInstanceId: $ruleInstanceId") }


                // **************** Check ConflictStrategy start ******************
                val conflictStrategy: ConflictStrategy = rule.conflictPolicy
                val isThisRuleAlreadyRunning =
                    synchronized(jobs) { jobs.any { it.srcId == rule.id && it.instanceId == ruleInstanceId } }
                if (isThisRuleAlreadyRunning) {
                    synchronized(jobs) {
                        logger.logOnDebug { ("isThisRuleAlreadyRunning is true, jobs: ${jobs.joinToString()}") }
                    }
                }
                logger.logOnDebug { ("${rule.title} conflictStrategy: $conflictStrategy, isThisRuleAlreadyRunning: $isThisRuleAlreadyRunning") }
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
                            logger.logOnDebug { ("Skip this rule actions since ConflictStrategy_SkipNew") }
                            false
                        }
                    }
                }
                logger.logOnDebug { ("shouldExecuteActions for rule ${rule.title} $shouldExecuteActions") }
                // ****************** Check ConflictStrategy end ***************************

                if (shouldExecuteActions) {
                    withQuitEvaPolicyOO { add(rule.id) }

                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(rule.id)
                        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                        .setSrcTitle(rule.title)
                        .setSerialNumber(nextEvaluateContextSerialNumber())
                        .build()

                    val job = coroutineScope.launchSafety("Fire-NTD") {
                        val parameterInputs =
                            rule.parametersList.map { it.asDefaultInput() }
                        // Launch Start
                        withContext(Dispatchers.IO) {
                            // Match condition
                            val conditionRes: Boolean = if (rule.conditionsList.isEmpty()) {
                                logger.logOnDebug { ("this rule: ${rule.title} has no condition, assume match!") }
                                true
                            } else {
                                conditionEvaluator.evaluate(
                                    evaluateContext = evaluateContext,
                                    condOp = rule.condOp,
                                    condOpPayload = rule.condOpPayload,
                                    conditions = rule.conditionsList,
                                    contextData = contextData,
                                    // No local var for a Rule condition.
                                    localVars = LocalVars.obtain(),
                                    parameterInputs = parameterInputs
                                )
                            }

                            logger.logOnDebug { ("conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes") }

                            if (conditionRes) {
                                // Update TheXXTimeToday state.
                                updateTheXXTimeToday(shortXContext, rule.conditionsList)

                                val localVars = LocalVars.obtain()
                                runActionsJobLocked(
                                    reason = "Fire#Rule-Actions",
                                    ruleTitleForLog = rule.title,
                                    asyncMode = rule.asyncMode,
                                    muteX = rule.muteX,
                                    actionsToRun = rule.actionsList,
                                    evaluateContext = evaluateContext,
                                    contextData = contextData,
                                    localVars = localVars,
                                    parameterInputs = parameterInputs,
                                    actionAsyncMode = rule.actionAsyncMode
                                )
                            } else {
                                logger.logOnDebug { ("Won't runActions since condition not match.") }
                            }
                        }
                        // Launch END.
                    }

                    logger.logOnDebug { ("Created job(ins: $ruleInstanceId): $job for rule: ${rule.title}") }
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
                        logger.logOnDebug { ("job(ins: $ruleInstanceId) for rule: ${rule.title} complete, err: $it") }
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
                logger.logOnDebug { ("fire, not match for rule it self: ${fact.javaClass} ${rule.title}, will check it's quit.") }

                val quit = rule.quit
                if (quit.isEnabled && checkQuitEvaluatePolicyForRule(
                        quit.evaluatePolicy,
                        rule.id
                    ) { activeJobs().map { it.srcId } }
                ) {
                    val quitFactMatcherResult = factMatcher.match(fact, quit.factsList)
                    logger.logOnDebug { ("quitFactMatcherResult: $quitFactMatcherResult") }
                    if (quitFactMatcherResult.isMatch) {
                        logger.logOnDebug { ("$LOG_TAG_RULE_QUIT fire, rule-quit matched: ${fact.javaClass} ${rule.title}") }
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
                            .select(
                                { rule.ruleInstanceIdGenerator },
                                {
                                    RuleInstanceIdGenerator_Static.newBuilder().setInsId(rule.id)
                                        .build()
                                        .pack_()
                                })
                        logger.logOnDebug { ("quit, ruleInsIdGen: $ruleInsIdGen") }
                        val ruleInstanceId = with(RuleInstanceIdGenerator) {
                            ruleInsIdGen.generate(androidContext, shortXContext, contextData)
                        } ?: "FALLBACK_${rule.id}"
                        logger.logOnDebug { ("quit, ruleInstanceId: $ruleInstanceId") }

                        coroutineScope.launchSafety("fire-Nick") {
                            // Launch start
                            withContext(Dispatchers.IO) {
                                val parameterInputs =
                                    rule.parametersList.map { it.asDefaultInput() }
                                val conditionRes = if (quit.conditionsList.isEmpty()) {
                                    logger.logOnDebug { ("$LOG_TAG_RULE_QUIT quit for rule: ${rule.title} has no condition, assume match!") }
                                    true
                                } else {
                                    conditionEvaluator.evaluate(
                                        evaluateContext = evaluateContext,
                                        condOp = quit.condOp,
                                        condOpPayload = quit.condOpPayload,
                                        conditions = quit.conditionsList,
                                        contextData = contextData,
                                        // No local var for a Rule condition.
                                        localVars = LocalVars.obtain(),
                                        parameterInputs = parameterInputs
                                    )
                                }
                                logger.logOnDebug { ("$LOG_TAG_RULE_QUIT conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes") }
                                if (conditionRes) {
                                    logger.logOnDebug { ("$LOG_TAG_RULE_QUIT about to quit ${rule.title}") }

                                    // DO NOT cancel ourself...
                                    cancelJobs("Rule quit(退出指令)") {
                                        it.srcId == rule.id && it.instanceId == ruleInstanceId
                                    }

                                    updateTheXXTimeToday(shortXContext, quit.conditionsList)

                                    runActionsJobLocked(
                                        reason = "Fire#Rule-Action-Quit",
                                        ruleTitleForLog = "$LOG_TAG_RULE_QUIT-${rule.title}",
                                        asyncMode = rule.asyncMode,
                                        muteX = rule.muteX,
                                        actionsToRun = quit.actionsList,
                                        evaluateContext = evaluateContext,
                                        contextData = contextData,
                                        localVars = localVars,
                                        parameterInputs = parameterInputs,
                                        actionAsyncMode = quit.actionAsyncMode
                                    )

                                    withQuitEvaPolicyOO { remove(evaluateContext.srcId) }
                                }
                                // Launch End
                            }
                        }
                    }
                }

            }
        }

        das.filterHasQuit()
            .filterDirectActionQuitEvaluatePolicy()
            .forEach { da ->
                logger.logOnDebug { ("fire, check da quit: ${fact.javaClass} ${da.title}") }

                val quit = da.quit

                if (quit.isEnabled) {
                    val quitFactMatcherResult = factMatcher.match(fact, quit.factsList)
                    logger.logOnDebug { ("quitFactMatcherResult: $quitFactMatcherResult") }
                    if (quitFactMatcherResult.isMatch) {
                        logger.logOnDebug { ("$LOG_TAG_RULE_QUIT fire, da-quit matched: ${fact.javaClass} ${da.title}") }
                        anyMatch = true

                        val contextData = ContextData.obtain(initialContextDataLazy())
                        if (quitFactMatcherResult is FactMatcher.MatcherResult.Match) {
                            contextData.add(
                                GenericContextData.CONTEXT_FACT_TAG,
                                quitFactMatcherResult.factTag
                            )
                        }
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId(da.id)
                            .setSrcEvent(ContextSrcEvent.ContextSrc_DirectActionQuit)
                            .setSrcTitle(da.title)
                            .setSerialNumber(nextEvaluateContextSerialNumber())
                            .build()
                        val localVars = LocalVars.obtain()

                        // Rule Instance ID
                        val ruleInsIdGen: ProtoAny =
                            RuleInstanceIdGenerator_Static.newBuilder().setInsId(da.id)
                                .build()
                                .pack_()
                        logger.logOnDebug { ("quit, ruleInsIdGen: $ruleInsIdGen") }
                        val ruleInstanceId = with(RuleInstanceIdGenerator) {
                            ruleInsIdGen.generate(androidContext, shortXContext, contextData)
                        } ?: "FALLBACK_${da.id}"
                        logger.logOnDebug { ("quit, ruleInstanceId: $ruleInstanceId") }

                        coroutineScope.launchSafety("fire-Tornaco") {
                            // Launch start
                            withContext(Dispatchers.IO) {
                                val parameterInputs =
                                    da.parametersList.map { it.asDefaultInput() }
                                val conditionRes = if (quit.conditionsList.isEmpty()) {
                                    logger.logOnDebug { ("$LOG_TAG_RULE_QUIT quit for da: ${da.title} has no condition, assume match!") }
                                    true
                                } else {
                                    conditionEvaluator.evaluate(
                                        evaluateContext = evaluateContext,
                                        condOp = quit.condOp,
                                        condOpPayload = quit.condOpPayload,
                                        conditions = quit.conditionsList,
                                        contextData = contextData,
                                        // No local var for a DA quit condition.
                                        localVars = LocalVars.obtain(),
                                        parameterInputs = parameterInputs
                                    )
                                }
                                logger.logOnDebug { ("$LOG_TAG_RULE_QUIT conditionRes for  ${fact.javaClass} ${da.title} is: $conditionRes") }
                                if (conditionRes) {
                                    logger.logOnDebug { ("$LOG_TAG_RULE_QUIT about to quit ${da.title}") }

                                    // DO NOT cancel ourself...
                                    cancelJobs("Rule quit(退出指令)") {
                                        it.srcId == da.id && it.instanceId == ruleInstanceId
                                    }

                                    updateTheXXTimeToday(shortXContext, quit.conditionsList)

                                    runActionsJobLocked(
                                        reason = "Fire#DA-Quit-Actions",
                                        ruleTitleForLog = "$LOG_TAG_RULE_QUIT-${da.title}",
                                        asyncMode = da.asyncMode,
                                        muteX = da.muteX,
                                        actionsToRun = quit.actionsList,
                                        evaluateContext = evaluateContext,
                                        contextData = contextData,
                                        localVars = localVars,
                                        parameterInputs = parameterInputs,
                                        actionAsyncMode = quit.actionAsyncMode
                                    )

                                    logger.logOnDebug { ("Remove from canEvaluateQuitDAInsWhenQuitPolicyIsEvaluateOnlyOnce: ${evaluateContext.srcId}") }

                                    withQuitEvaPolicyOO {
                                        remove(evaluateContext.srcId)
                                    }
                                }
                                // Launch End
                            }
                        }
                    }
                }

            }

        return anyMatch
    }

    private suspend fun runActionsJobLocked(
        reason: String,
        ruleTitleForLog: String,
        asyncMode: AsyncMode,
        muteX: Mutex,
        actionsToRun: List<ProtoAny>,
        actionAsyncMode: ActionAsyncMode,
        evaluateContext: EvaluateContext,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>
    ) {
        // By default, execute action in IO
        logger.logOnDebug { ("job for rule: $ruleTitleForLog start. ($reason)") }
        val runActions: suspend () -> Unit = {
            realRunActions(
                actionsToRun = actionsToRun,
                ruleTitleForLog = ruleTitleForLog,
                evaluateContext = evaluateContext,
                contextData = contextData,
                localVars = localVars,
                parameterInputs = parameterInputs,
                actionAsyncMode = actionAsyncMode,
            )
        }

        when (asyncMode) {
            AsyncMode.AsyncMode_Async, AsyncMode.UNRECOGNIZED -> {
                logger.logOnDebug { ("Run actions with Async") }
                runActions()
            }

            AsyncMode.AsyncMode_Sync_InRule -> {
                logger.logOnDebug { ("Run actions with Sync_InRule: $muteX") }
                muteX.withLock {
                    runActions()
                }
            }

            else -> {
                logger.logOnDebug { ("Run actions with globalMuteX") }
                globalMuteX.withLock {
                    runActions()
                }
            }
        }
    }

    override fun evaluateActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        actionAsyncMode: ActionAsyncMode,
        initialContextData: Map<String, Any>,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>,
        conflictStrategy: ConflictStrategy,
        onComplete: ((contextData: Map<String, Any?>) -> Unit)?
    ) {
        val contextData = ContextData.obtain(initialContextData)
        val isAlreadyRunning = synchronized(jobs) { jobs.any { it.srcId == context.srcId } }
        logger.logOnDebug { ("evaluateActions, ${context.srcTitle} conflictStrategy: $conflictStrategy, isAlreadyRunning: $isAlreadyRunning") }

        if (isAlreadyRunning) {
            when (conflictStrategy) {
                ConflictStrategy.ConflictStrategy_ExecuteBoth, ConflictStrategy.UNRECOGNIZED -> {
                    // Fall through.
                }

                ConflictStrategy.ConflictStrategy_ReplaceOld -> {
                    cancelJobs("ConflictStrategy_ReplaceOld(重复策略，取消旧任务)") { it.srcId == context.srcId }
                }

                ConflictStrategy.ConflictStrategy_SkipNew -> {
                    logger.logOnDebug { ("Skip this rule actions since ConflictStrategy_SkipNew") }
                    onComplete?.invoke(emptyMap())
                    return
                }
            }
        }
        logger.logOnDebug { ("Add to canEvaluateQuitDAInsWhenQuitPolicyIsEvaluateOnlyOnce: ${context.srcId}") }
        withQuitEvaPolicyOO { add(context.srcId) }

        val job = coroutineScope.launchSafety("EvaluateActions.Wend") {
            withContext(Dispatchers.IO) {
                logger.logOnDebug { ("evaluateActions, job for id: ${context.srcId} start.") }
                realRunActions(
                    actionsToRun = actions,
                    ruleTitleForLog = context.srcTitle,
                    evaluateContext = context,
                    contextData = contextData,
                    localVars = localVars,
                    parameterInputs = parameterInputs,
                    actionAsyncMode = actionAsyncMode
                )
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
                    tags = setOf(
                        context.srcEvent,
                        context.srcEventPayload1,
                        context.srcEventPayload2
                    )
                )
            )
        }
        onJobStarted(context.srcId)
        job.invokeOnCompletion {
            logger.logOnDebug { ("job for id: ${context.srcId} complete.") }
            synchronized(jobs) {
                jobs.removeIf {
                    it.job == job
                }
            }
            onJobCompleted(context.srcId)
            onComplete?.let {
                it(contextData.map.toMap())
            }
            contextData.recycle()
        }
    }

    private suspend fun realRunActions(
        actionsToRun: List<ProtoAny>,
        ruleTitleForLog: String,
        evaluateContext: EvaluateContext,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>,
        actionAsyncMode: ActionAsyncMode
    ) {
        when (actionAsyncMode) {
            ActionAsyncMode.ActionAsyncMode_Async -> {
                realRunActionsInAsyncMode(
                    actionsToRun,
                    ruleTitleForLog,
                    evaluateContext,
                    contextData,
                    localVars,
                    parameterInputs
                )
            }

            else -> {
                realRunActionsInSyncMode(
                    actionsToRun,
                    ruleTitleForLog,
                    evaluateContext,
                    contextData,
                    localVars,
                    parameterInputs
                )
            }
        }
    }

    private suspend fun realRunActionsInAsyncMode(
        actionsToRun: List<ProtoAny>,
        ruleTitleForLog: String,
        evaluateContext: EvaluateContext,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>
    ) {
        logger.logOnDebug { ("realRunActionsInAsyncMode, about to execute ${actionsToRun.size} actions for $ruleTitleForLog") }
        coroutineScope {
            actionsToRun.map {
                async {
                    logger.logOnDebug { ("realRunActionsInAsyncMode, about to execute action: ${it.typeUrl}") }
                    val evaRes = actionEvaluator.evaluate(
                        evaluateContext = evaluateContext,
                        action = it,
                        contextData = contextData,
                        localVars = localVars,
                        parameterInputs = parameterInputs
                    )
                    logger.logOnDebug { ("realRunActionsInAsyncMode, action evaluate res: $evaRes") }
                }
            }.awaitAll()
            logger.logOnDebug { ("realRunActionsInAsyncMode, all complete.") }
        }
    }

    private suspend fun realRunActionsInSyncMode(
        actionsToRun: List<ProtoAny>,
        ruleTitleForLog: String,
        evaluateContext: EvaluateContext,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>
    ) {
        kotlin.run loop@{
            logger.logOnDebug { ("realRunActionsInSyncMode, about to execute ${actionsToRun.size} actions for $ruleTitleForLog") }
            actionsToRun.forEach {
                logger.logOnDebug { ("realRunActionsInSyncMode, about to execute action: ${it.typeUrl}") }
                val evaRes = actionEvaluator.evaluate(
                    evaluateContext = evaluateContext,
                    action = it,
                    contextData = contextData,
                    localVars = localVars,
                    parameterInputs = parameterInputs
                )
                logger.logOnDebug { ("realRunActionsInSyncMode, action evaluate res: $evaRes") }
                if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                    logger.e(evaRes.error, "realRunActionsInSyncMode, evaluate fail")
                    if (evaRes.shouldContinue) {
                        logger.logOnDebug { ("realRunActionsInSyncMode, action on error is continue execute.") }
                    } else {
                        return@loop
                    }
                } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                    logger.w("realRunActionsInSyncMode, evaluate break!")
                    return@loop
                } else if (evaRes is ActionEvaluator.EvaluateResult.Disabled) {
                    logger.logOnDebug { ("realRunActionsInSyncMode, action is disabled: ${evaRes.action.typeUrl}") }
                }
            }
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
                evaluateContext = context,
                condOp = condOp,
                condOpPayload = condOpPayload,
                conditions = conditions,
                contextData = ContextData.obtain(initialContextData),
                localVars = LocalVars.obtain(),
                parameterInputs = emptyList()
            )
        }
    }

    override fun cancelJobs(reason: String, filter: (JobRecord) -> Boolean) {
        synchronized(jobs) {
            val canceled = jobs.filter(filter).onEach {
                logger.logOnDebug { ("cancel job: $it, reason: $reason") }
                it.job.cancel(CancellationException(reason))
                logger.logOnDebug { ("After call cancel, job isCanceled: ${it.job.isCancelled}, job isActive: ${it.job.isActive}") }
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

    private fun List<DirectAction>.filterDirectActionQuitEvaluatePolicy(): List<DirectAction> {
        val jobs = activeJobs().map { it.srcId }
        return filter { da ->
            val policy: Quit_EvaluatePolicy = da.quit.evaluatePolicy
            checkQuitEvaluatePolicyForDA(policy, da.id) { jobs }
        }
    }

    private fun checkQuitEvaluatePolicyForDA(
        policy: Quit_EvaluatePolicy,
        srcId: String,
        jobIdProvider: () -> List<String>
    ): Boolean {
        logger.logOnDebug { ("checkQuitEvaluatePolicyForDA: srcId $srcId") }
        withQuitEvaPolicyOO { logger.logOnDebug { ("checkQuitEvaluatePolicyForDA: can...OnlyOnce $this") } }
        return when (policy) {
            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Only_When_Is_Running, Quit_EvaluatePolicy.UNRECOGNIZED -> {
                val jobs = jobIdProvider()
                logger.logOnDebug { ("checkQuitEvaluatePolicyForDA: jobs $jobs") }
                jobs.contains(srcId)
            }

            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Always -> {
                true
            }

            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Once -> {
                withQuitEvaPolicyOO { contains(srcId) }
            }
        }
    }

    private fun checkQuitEvaluatePolicyForRule(
        policy: Quit_EvaluatePolicy,
        srcId: String,
        jobIdProvider: () -> List<String>
    ): Boolean {
        logger.logOnDebug { ("checkQuitEvaluatePolicyForRule: srcId $srcId") }
        withQuitEvaPolicyOO {
            logger.logOnDebug { ("checkQuitEvaluatePolicyForRule: can...OnlyOnce $this") }
        }
        return when (policy) {
            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Only_When_Is_Running -> {
                val jobs = jobIdProvider()
                logger.logOnDebug { ("checkQuitEvaluatePolicyForRule: jobs $jobs") }
                jobs.contains(srcId)
            }

            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Always, Quit_EvaluatePolicy.UNRECOGNIZED -> {
                true
            }

            Quit_EvaluatePolicy.Quit_EvaluatePolicy_Once -> {
                withQuitEvaPolicyOO { contains(srcId) }
            }
        }
    }
}

private fun List<Rule>.filterInvalid() =
    filter { it.actionsList.isNotEmpty() || it.quit.isEnabled }

private fun List<DirectAction>.filterHasQuit() =
    filter { it.quit.isEnabled }

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

object JobTags