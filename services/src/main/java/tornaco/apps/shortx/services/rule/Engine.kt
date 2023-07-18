package tornaco.apps.shortx.services.rule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.launchSafety
import java.util.concurrent.ConcurrentHashMap

const val CONDITION_WAIT_TIMEOUT = 100L

const val LOG_TAG_RULE_QUIT = "Rule-Quit"

interface FactMatcher {
    fun match(actualFact: ProtoMessage, ruleFacts: List<ProtoAny>): Boolean
}

interface ConditionEvaluator {
    suspend fun evaluate(
        condition: ProtoAny,
        contextData: ContextData
    ): EvaluateResult

    suspend fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData,
    ): Boolean

    sealed interface EvaluateResult {
        data class Success(val condition: ProtoAny, val match: Boolean) : EvaluateResult
        data class Fail(val condition: ProtoAny, val error: Throwable) :
            EvaluateResult

        object Break : EvaluateResult
    }
}

fun ConditionEvaluator.EvaluateResult.isMatch() =
    this is ConditionEvaluator.EvaluateResult.Success && this.match

interface ActionEvaluator {
    suspend fun evaluate(
        logTag: String,
        action: ProtoAny,
        contextData: ContextData
    ): EvaluateResult

    sealed interface EvaluateResult {
        data class Success(val action: ProtoAny) : EvaluateResult
        data class Fail(val shouldContinue: Boolean, val action: ProtoAny, val error: Throwable) :
            EvaluateResult

        data class Break(val action: ProtoAny) : EvaluateResult
        data class Disabled(val action: ProtoAny) : EvaluateResult
    }
}

class StopExecuteException(override val message: String) : RuntimeException()

interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher
    val scope: CoroutineScope

    fun shouldFire(rules: List<Rule>, fact: ProtoMessage): Boolean
    fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        initialContextData: Map<String, Any> = emptyMap()
    ): Boolean

    fun evaluate(
        id: String,
        logTag: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap()
    )

    fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData,
    ): Boolean

    fun cancelJobs(reason: String, filter: (JobRecord) -> Boolean)

    fun activeJobs(): List<JobRecord>
}

class DefaultRuleEngine(
    override val scope: CoroutineScope,
    override val factMatcher: FactMatcher,
    override val conditionEvaluator: ConditionEvaluator,
    override val actionEvaluator: ActionEvaluator,
    private val onJobStarted: (String) -> Unit,
    private val onJobCompleted: (String) -> Unit,
    private val onJobCancelled: (String) -> Unit,
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

    override fun shouldFire(rules: List<Rule>, fact: ProtoMessage): Boolean {
        val startTimeMillis = System.currentTimeMillis()
        return rules.filterInvalid().any {
            shouldFireRuleForFact(fact, it) || shouldFireRuleZQuitForFact(fact, it)
        }.also {
            logger.d("shouldFire check taken: ${System.currentTimeMillis() - startTimeMillis}ms")
        }
    }

    private fun shouldFireRuleForFact(
        fact: ProtoMessage,
        rule: Rule
    ) = factMatcher.match(fact, rule.factsList) && runBlocking {
        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
            conditionEvaluator.evaluate(
                logTag = rule.title,
                condOp = rule.condOp,
                condOpPayload = rule.condOpPayload,
                conditions = rule.conditionsList,
                contextData = ContextData.obtain()
            )
        }
    } ?: defaultCondRes(rule)

    private fun shouldFireRuleZQuitForFact(
        fact: ProtoMessage,
        rule: Rule
    ) = rule.quit.isEnabled && factMatcher.match(fact, rule.quit.factsList) && runBlocking {
        withTimeoutOrNull(CONDITION_WAIT_TIMEOUT) {
            conditionEvaluator.evaluate(
                logTag = "${rule.title}-$LOG_TAG_RULE_QUIT",
                condOp = rule.quit.condOp,
                condOpPayload = rule.quit.condOpPayload,
                conditions = rule.quit.conditionsList,
                contextData = ContextData.obtain()
            )
        }
    } ?: defaultCondRes(rule)

    // Should call in Handler to make it Thread safe.
    override fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        initialContextData: Map<String, Any>
    ): Boolean {
        logger.d("fire: ${fact.javaClass}")
        var anyMatch = false
        rules.filterInvalid().forEach { rule ->

            // Try fire for Rule.
            if (factMatcher.match(fact, rule.factsList)) {
                logger.d("fire, rule matched: ${fact.javaClass} ${rule.title}")
                anyMatch = true
                val contextData = ContextData.obtain(initialContextData)
                contextData.add(ContextDataMapping.Context.contextSrcId.name, rule.id)
                contextData.add(ContextDataMapping.Context.contextSrcTitle.name, rule.title)
                contextData.add(
                    ContextDataMapping.Context.contextSrcEvent.name,
                    ContextSrcEvent.ContextSrc_Rule
                )

                val job = scope.launchSafety {
                    logger.d("job for rule: ${rule.title} start.")
                    val conditionRes = if (rule.conditionsList.isEmpty()) {
                        logger.d("this rule: ${rule.title} has no condition, assume match!")
                        true
                    } else {
                        conditionEvaluator.evaluate(
                            logTag = rule.title,
                            condOp = rule.condOp,
                            condOpPayload = rule.condOpPayload,
                            conditions = rule.conditionsList,
                            contextData = contextData
                        )
                    }
                    logger.d("conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes")
                    if (conditionRes) {
                        val runActions: suspend () -> Unit = {
                            kotlin.run loop@{
                                logger.d("about to execute ${rule.actionsList.size} actions for ${fact.javaClass} ${rule.title}")
                                rule.actionsList.forEach {
                                    val evaRes = actionEvaluator.evaluate(
                                        rule.title,
                                        it,
                                        contextData
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

                        when (rule.asyncMode) {
                            AsyncMode.AsyncMode_Async, AsyncMode.UNRECOGNIZED -> {
                                logger.d("Run actions with Async")
                                runActions()
                            }

                            AsyncMode.AsyncMode_Sync_InRule -> {
                                val muteX = rule.muteX
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
                }
                logger.d("Created job: $job")
                jobs.add(JobRecord(rule.id, job))
                onJobStarted(rule.id)
                job.invokeOnCompletion {
                    logger.d("job for rule: ${rule.title} complete, err: $it")
                    jobs.remove(JobRecord(rule.id, job))
                    contextData.recycle()
                    onJobCompleted(rule.id)
                }
            } else {
                logger.d("fire, not match for rule it self: ${fact.javaClass} ${rule.title}, will check it's quit.")

                val quit = rule.quit
                if (quit.isEnabled && factMatcher.match(fact, quit.factsList)) {
                    logger.d("$LOG_TAG_RULE_QUIT fire, rule-quit matched: ${fact.javaClass} ${rule.title}")
                    anyMatch = true

                    val contextData = ContextData.obtain(initialContextData)
                    contextData.add(ContextDataMapping.Context.contextSrcId.name, rule.id)
                    contextData.add(ContextDataMapping.Context.contextSrcTitle.name, rule.title)
                    contextData.add(
                        ContextDataMapping.Context.contextSrcEvent.name,
                        ContextSrcEvent.ContextSrc_RuleQuit
                    )

                    scope.launchSafety {
                        val conditionRes = if (quit.conditionsList.isEmpty()) {
                            logger.d("$LOG_TAG_RULE_QUIT quit for rule: ${rule.title} has no condition, assume match!")
                            true
                        } else {
                            conditionEvaluator.evaluate(
                                logTag = "${rule.title}-$LOG_TAG_RULE_QUIT",
                                condOp = quit.condOp,
                                condOpPayload = quit.condOpPayload,
                                conditions = quit.conditionsList,
                                contextData = contextData
                            )
                        }
                        logger.d("$LOG_TAG_RULE_QUIT conditionRes for  ${fact.javaClass} ${rule.title} is: $conditionRes")
                        if (conditionRes) {
                            logger.d("$LOG_TAG_RULE_QUIT about to quit ${rule.title}")
                            cancelJobs("Rule quit") { it.id == rule.id }
                        }
                    }
                }
            }
        }
        return anyMatch
    }

    override fun evaluate(
        id: String,
        logTag: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any>
    ) {
        val contextData = ContextData.obtain(initialContextData)
        val job = scope.launchSafety {
            logger.d("Evaluate actions, job for id: $id start.")
            kotlin.run loop@{
                actions.forEach {
                    val evaRes = actionEvaluator.evaluate(logTag, it, contextData)
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
        jobs.add(JobRecord(id, job))
        onJobStarted(id)
        job.invokeOnCompletion {
            logger.d("job for id: $id complete.")
            jobs.remove(JobRecord(id, job))
            contextData.recycle()
            onJobCompleted(id)
        }
    }

    // This API is exposed for App check condition on Edit.
    // So let is run blocking.
    override fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData
    ): Boolean {
        return runBlocking {
            conditionEvaluator.evaluate(logTag, condOp, condOpPayload, conditions, contextData)
        }
    }

    override fun cancelJobs(reason: String, filter: (JobRecord) -> Boolean) {
        val canceled = jobs.filter(filter).onEach {
            logger.d("cancel job: $it, reason: $reason")
            it.job.cancel()
            onJobCancelled(it.id)
        }
        jobs.removeAll(canceled)
    }

    override fun activeJobs(): List<JobRecord> {
        return jobs.filter { it.job.isActive }
    }
}

private fun List<Rule>.filterInvalid() = filter { it.actionsList.isNotEmpty() }

data class JobRecord(val id: String, val job: Job)