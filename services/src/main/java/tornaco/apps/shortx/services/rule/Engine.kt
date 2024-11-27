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
        checkCondition: Boolean
    ): Boolean

    fun fire(
        fact: ProtoMessage,
        rules: List<Rule>,
        das: List<DirectAction>,
        initialContextDataLazy: () -> Map<String, Any>
    ): Boolean

    fun evaluateActions(
        context: EvaluateContext,
        actions: List<ProtoAny>,
        actionAsyncMode: ActionAsyncMode,
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