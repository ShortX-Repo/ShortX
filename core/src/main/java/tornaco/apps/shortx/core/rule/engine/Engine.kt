package tornaco.apps.shortx.core.rule.engine

import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.util.Logger

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
        contextMap: Map<String, Any> = emptyMap()
    )
}

class StopExecuteException(override val message: String) : RuntimeException()

interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher

    fun shouldFire(rules: List<Rule>, fact: ProtoMessage): Boolean
    suspend fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        contextMap: Map<String, Any> = emptyMap()
    )

    suspend fun evaluate(
        logTag: String,
        actions: List<ProtoAny>,
        contextMap: Map<String, Any> = emptyMap()
    )
}

interface EngineListener {
    fun onFactMatch(rule: Rule, fact: ProtoAny) {}
    fun onFactNotMatch(rule: Rule, fact: ProtoAny) {}
    fun onConditionMatch(rule: Rule) {}
    fun onConditionNotMatch(rule: Rule) {}
    fun beforeEvaluateAction(rule: Rule, action: ProtoAny) {}
    fun afterEvaluateAction(rule: Rule, action: ProtoAny) {}
}

class LogEngineListener(val tag: String) : EngineListener {
    private val logger = Logger(tag)

    override fun onFactMatch(rule: Rule, fact: ProtoAny) {
        logger.d("onFactMatch: ${rule.title} ${fact.javaClass.simpleName}")
    }

    override fun onFactNotMatch(rule: Rule, fact: ProtoAny) {
        logger.d("onFactNotMatch: ${rule.title} ${fact.javaClass.simpleName}")
    }

    override fun onConditionMatch(rule: Rule) {
        logger.d("onConditionMatch: ${rule.title}")
    }

    override fun onConditionNotMatch(rule: Rule) {
        logger.d("onConditionNotMatch: ${rule.title}")
    }

    override fun beforeEvaluateAction(rule: Rule, action: ProtoAny) {
        logger.d("beforeEvaluateAction: ${rule.title} ${action.javaClass.simpleName}")
    }

    override fun afterEvaluateAction(rule: Rule, action: ProtoAny) {
        logger.d("afterEvaluateAction: ${rule.title} ${action.javaClass.simpleName}")
    }
}

class DefaultRuleEngine(
    override val factMatcher: FactMatcher,
    override val conditionEvaluator: ConditionEvaluator,
    override val actionEvaluator: ActionEvaluator,
    private val listener: EngineListener = LogEngineListener("RuleEngine"),
) : Engine {
    private val logger = Logger("DefaultRuleEngine")

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

    override suspend fun fire(fact: ProtoMessage, rules: List<Rule>, contextMap: Map<String, Any>) {
        logger.d("fire: ${fact.javaClass}")
        rules.filterInvalid().forEach { rule ->
            if (factMatcher.match(fact, rule.fact)) {
                listener.onFactMatch(rule, rule.fact)
                val conditionRes = conditionEvaluator.evaluate(
                    rule.title,
                    rule.condOp,
                    rule.condOpPayload,
                    rule.conditionsList
                )
                if (conditionRes) {
                    listener.onConditionMatch(rule)
                    kotlin.run loop@{
                        rule.actionsList.forEach {
                            listener.beforeEvaluateAction(rule, it)
                            kotlin.runCatching {
                                actionEvaluator.evaluate(
                                    rule.title,
                                    it,
                                    contextMap
                                )
                            }
                                .onFailure { err ->
                                    logger.e(err, "Error evaluate action, stop execution")
                                    return@loop
                                }
                            listener.afterEvaluateAction(rule, it)
                        }
                    }
                } else {
                    listener.onConditionNotMatch(rule)
                }
            } else {
                listener.onFactNotMatch(rule, rule.fact)
            }
        }
    }

    override suspend fun evaluate(
        logTag: String,
        actions: List<ProtoAny>,
        contextMap: Map<String, Any>
    ) {
        kotlin.run loop@{
            actions.forEach {
                kotlin.runCatching { actionEvaluator.evaluate(logTag, it, contextMap) }
                    .onFailure { err ->
                        logger.e(err, "Error evaluate action, stop execution")
                        return@loop
                    }
            }
        }
    }
}

private fun List<Rule>.filterInvalid() = filter {
    it.conditionsList.isNotEmpty() && it.actionsList.isNotEmpty()
}