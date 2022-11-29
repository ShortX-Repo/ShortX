package tornaco.apps.shortx.core.rule.engine

import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.util.Logger

interface FactMatcher {
    fun match(fact: ProtoMessage, targetFact: ProtoAny): Boolean
}

interface ConditionEvaluator {
    suspend fun evaluate(condition: ProtoAny): Boolean
}

interface ActionEvaluator {
    suspend fun evaluate(action: ProtoAny)
}

interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher

    fun hasAnyRuleMatchFact(rules: List<Rule>, fact: ProtoMessage): Boolean
    suspend fun fire(fact: ProtoMessage, rules: List<Rule>)
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
        logger.d("onFactMatch: $rule $fact")
    }

    override fun onFactNotMatch(rule: Rule, fact: ProtoAny) {
        logger.d("onFactNotMatch: $rule $fact")
    }

    override fun onConditionMatch(rule: Rule) {
        logger.d("onConditionMatch: $rule")
    }

    override fun onConditionNotMatch(rule: Rule) {
        logger.d("onConditionNotMatch: $rule")
    }

    override fun beforeEvaluateAction(rule: Rule, action: ProtoAny) {
        logger.d("beforeEvaluateAction: $rule $action")
    }

    override fun afterEvaluateAction(rule: Rule, action: ProtoAny) {
        logger.d("afterEvaluateAction: $rule $action")
    }
}

class DefaultRuleEngine(
    override val factMatcher: FactMatcher,
    override val conditionEvaluator: ConditionEvaluator,
    override val actionEvaluator: ActionEvaluator,
    private val listener: EngineListener = LogEngineListener("RuleEngine"),
) : Engine {
    private val logger = Logger("DefaultRuleEngine")

    override fun hasAnyRuleMatchFact(rules: List<Rule>, fact: ProtoMessage): Boolean {
        return rules.filterInvalid().firstOrNull {
            factMatcher.match(fact, it.fact)
        } != null
    }

    override suspend fun fire(fact: ProtoMessage, rules: List<Rule>) {
        logger.d("fire: ${fact.javaClass}")
        rules.filterInvalid().forEach { rule ->
            if (factMatcher.match(fact, rule.fact)) {
                listener.onFactMatch(rule, rule.fact)
                val conditionRes = rule.conditionsList.all {
                    conditionEvaluator.evaluate(it)
                }
                if (conditionRes) {
                    listener.onConditionMatch(rule)
                    rule.actionsList.forEach {
                        listener.beforeEvaluateAction(rule, it)
                        actionEvaluator.evaluate(it)
                        listener.afterEvaluateAction(rule, it)
                    }
                } else {
                    listener.onConditionNotMatch(rule)
                }
            } else {
                listener.onFactNotMatch(rule, rule.fact)
            }
        }
    }
}

private fun List<Rule>.filterInvalid() = filter {
    it.conditionsList.isNotEmpty() && it.actionsList.isNotEmpty()
}