package tornaco.apps.shortx.services.rule

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluator
import tornaco.apps.shortx.services.rule.fact.FactMatcher

data class FireResult(val anyMatch: Boolean, val jobs: List<Job>)

interface Engine {
    val conditionEvaluator: ConditionEvaluator
    val actionEvaluator: ActionEvaluator
    val factMatcher: FactMatcher
    val coroutineScope: CoroutineScope

    val androidContext: Context
    val shortXContext: ShortXContext

    fun shouldFire(
        rules: List<Rule>,
        das: List<DirectAction>,
        fact: ProtoMessage,
        // For normal fact, no need to check condition
        // For KeyEvent, pls check condition before intercept system
        checkCondition: Boolean,
        // To prevent keyevent block by Fact:Any
        // Type urls.
        excludeFacts: Set<String>
    ): Boolean

    fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        das: List<DirectAction>,
        initialContextDataLazy: () -> Map<String, Any>
    ): FireResult

    fun evaluateActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        actionAsyncMode: ActionAsyncMode,
        initialContextData: Map<String, Any>,
        localVars: LocalVars = LocalVars.obtain(),
        parameterInputs: List<FuncParameterInput> = emptyList(),
        conflictStrategy: ConflictStrategy,
        onComplete: ((contextData: Map<String, Any?>) -> Unit)?
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