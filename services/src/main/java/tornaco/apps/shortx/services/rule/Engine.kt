package tornaco.apps.shortx.services.rule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.launchSafety

interface FactMatcher {
    fun match(actualFact: ProtoMessage, ruleFact: ProtoAny): Boolean
}

interface ConditionEvaluator {
    fun evaluate(
        logTag: String,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>
    ): Boolean
}

interface ActionEvaluator {
    suspend fun evaluate(
        logTag: String,
        action: ProtoAny,
        contextData: ContextData
    )
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
    )

    fun evaluate(
        id: String,
        logTag: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any> = emptyMap()
    )

    fun cancelJobs(filter: (JobRecord) -> Boolean)

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

    override fun shouldFire(rules: List<Rule>, fact: ProtoMessage): Boolean {
        return rules.filterInvalid().firstOrNull {
            factMatcher.match(fact, it.fact) && conditionEvaluator.evaluate(
                it.title,
                it.condOp,
                it.condOpPayload,
                it.conditionsList
            )
        } != null
    }

    override fun fire(fact: ProtoMessage, rules: List<Rule>, initialContextData: Map<String, Any>) {
        logger.d("fire: ${fact.javaClass}")
        rules.filterInvalid().forEach { rule ->
            if (factMatcher.match(fact, rule.fact)) {
                val contextData = ContextData.obtain(initialContextData)
                val job = scope.launchSafety {
                    logger.d("job for rule: ${rule.title} start.")
                    val conditionRes = conditionEvaluator.evaluate(
                        rule.title,
                        rule.condOp,
                        rule.condOpPayload,
                        rule.conditionsList
                    )
                    if (conditionRes) {
                        kotlin.run loop@{
                            rule.actionsList.forEach {
                                kotlin.runCatching {
                                    actionEvaluator.evaluate(
                                        rule.title,
                                        it,
                                        contextData
                                    )
                                }.onFailure { err ->
                                    logger.e(err, "Error evaluate action, stop execution")
                                    return@loop
                                }
                            }
                        }
                    }
                }
                jobs.add(JobRecord(rule.id, job))
                onJobStarted(rule.id)
                job.invokeOnCompletion {
                    logger.d("job for rule: ${rule.title} complete, err: $it")
                    jobs.remove(JobRecord(rule.id, job))
                    contextData.recycle()
                    onJobCompleted(rule.id)
                }
            }
        }
    }

    override fun evaluate(
        id: String,
        logTag: String,
        actions: List<ProtoAny>,
        initialContextData: Map<String, Any>
    ) {
        val contextData = ContextData.obtain(initialContextData)
        val job = scope.launchSafety {
            logger.d("job for id: $id start.")
            kotlin.run loop@{
                actions.forEach {
                    kotlin.runCatching { actionEvaluator.evaluate(logTag, it, contextData) }
                        .onFailure { err ->
                            logger.e(err, "Error evaluate action, stop execution")
                            return@loop
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

    override fun cancelJobs(filter: (JobRecord) -> Boolean) {
        val canceled = jobs.filter(filter).onEach {
            logger.d("cancel job: $it")
            it.job.cancel()
            onJobCancelled(it.id)
        }
        jobs.removeAll(canceled)
    }

    override fun activeJobs(): List<JobRecord> {
        return jobs.filter { it.job.isActive }
    }
}

private fun List<Rule>.filterInvalid() = filter {
    it.conditionsList.isNotEmpty() && it.actionsList.isNotEmpty()
}

data class JobRecord(val id: String, val job: Job)