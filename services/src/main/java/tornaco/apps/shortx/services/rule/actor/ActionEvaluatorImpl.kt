package tornaco.apps.shortx.services.rule.actor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import com.google.protobuf.Descriptors
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.ActionOnError
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluator
import tornaco.apps.shortx.services.rule.fact.FactMatcher
import kotlin.coroutines.cancellation.CancellationException

data class ActionAttrs(
    val id: String,
    val isDisabled: Boolean,
    val onError: ActionOnError
)


class ActionEvaluatorImpl(
    private val context: Context,
    private val uiContext: Context,
    private val shortXContext: ShortXContext,
    private val handler: Handler,
    private val evaluateStartListener: (actionId: String) -> Unit,
    private val evaluateResultListener: (ActionEvaluator.EvaluateResult) -> Unit,
    private val conditionEvaluator: ConditionEvaluator,
    private val factMatcher: FactMatcher,
) : ActionEvaluator {
    private val logger = Logger("ActionEvaluatorImpl")
    private val recursiveExecute: suspend (
        context: EvaluateContext,
        action: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>
    ) -> ActionEvaluator.EvaluateResult =
        { logTagR, actionR, contextDataR, localVars, parameterInputs ->
            evaluate(logTagR, actionR, contextDataR, localVars, parameterInputs)
        }

    @SuppressLint("MissingPermission")
    override suspend fun evaluate(
        evaluateContext: EvaluateContext,
        action: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>
    ): ActionEvaluator.EvaluateResult {
        val actor = requireNotNull(prebuiltActorFor(action)) {
            "Can not find Actor for: ${action.typeUrl}"
        }
        logger.d("evaluate: ${evaluateContext.srcTitle} - ${action.typeUrl}, actor: $actor")
        logger.logOnDebug("parameters: ${parameterInputs.joinToString { it.toLogString() }}")

        // Do not worry about unpack cost, protobuf has cache.
        val unPacked = action unpack_ actor.actionsCanHandle()
        val attrs = extractActionAttrs(unPacked)
        val actionId = attrs?.id.fallbackOnEmpty("Unknown-Action-Id")
        logger.d("Action attrs: $attrs")

        if (attrs != null && attrs.isDisabled) {
            return ActionEvaluator.EvaluateResult.Disabled(actionId, action, evaluateContext)
        }
        val shouldContinueOnError = attrs != null && attrs.onError == ActionOnError.Continue

        val wakeLockTag = "${action.typeUrl}_${System.currentTimeMillis()}:wakelock"
        val wakelock = if (actor.withWakelock) {
            context.powerManager.newWakeLock(
                PARTIAL_WAKE_LOCK,
                wakeLockTag
            ).apply {
                setReferenceCounted(false)
                // Action timeout is 1h
                acquire(60 * 60 * 1000)
            }
        } else null
        if (wakelock != null) {
            logger.w("Acquired wakelock: $wakeLockTag")
        }

        return kotlin.runCatching {
            evaluateStartListener(actionId)

            val startTimeMills = System.currentTimeMillis()
            logger.d("Action start evaluate [$actor]")

            val actorRes = actor.handle(
                HandleParams(
                    androidContext = context,
                    androidUiContext = uiContext,
                    shortXContext = shortXContext,
                    evaluateContext = evaluateContext,
                    action = action,
                    contextData = contextData,
                    localVars = localVars,
                    recursiveExecute = recursiveExecute,
                    conditionEvaluator = conditionEvaluator,
                    factMatcher = factMatcher,
                    parameterInputs = parameterInputs
                )
            )

            val timeTaken = System.currentTimeMillis() - startTimeMills
            logger.d("Action evaluated in ${timeTaken}ms [$actor]")

            ActionEvaluator.EvaluateResult.Success(
                actionId = actionId,
                action = action,
                log = actorRes.log,
                evaluateContext = evaluateContext
            )
        }.getOrElse { throwable ->
            // Clean up registered listeners for Actor usage...
            attrs?.let { shortXContext.rule.unregisterInternalFactListener(it.id) }

            if (throwable is BreakActionException) {
                ActionEvaluator.EvaluateResult.Break(
                    actionId,
                    action,
                    throwable.scope,
                    evaluateContext
                )
            } else {
                val isCanceledError = throwable is CancellationException
                if (isCanceledError) {
                    logger.i("Action is canceled, will not continue.")
                }
                ActionEvaluator.EvaluateResult.Fail(
                    shouldContinue = shouldContinueOnError && !isCanceledError,
                    actionId = actionId,
                    action = action,
                    error = throwable,
                    evaluateContext = evaluateContext
                )
            }
        }.apply {
            evaluateResultListener(this)
            if (wakelock != null) {
                wakelock.release()
                logger.w("Released wakelock: $wakeLockTag")
            }
        }
    }

    private fun extractActionAttrs(action: ProtoMessage): ActionAttrs? {
        return runCatching {
            val idDescriptor: Descriptors.FieldDescriptor =
                action.descriptorForType.findFieldByName("id")
            val id: String = action.getField(idDescriptor) as String

            val isDisabledDescriptor: Descriptors.FieldDescriptor =
                action.descriptorForType.findFieldByName("isDisabled")
            val isDisabled: Boolean = action.getField(isDisabledDescriptor) as Boolean

            val onErrorDescriptor: Descriptors.FieldDescriptor =
                action.descriptorForType.findFieldByName("actionOnError")
            val enumDescription: Descriptors.EnumValueDescriptor =
                action.getField(onErrorDescriptor) as Descriptors.EnumValueDescriptor
            val onError: ActionOnError = ActionOnError.forNumber(enumDescription.number)

            ActionAttrs(id, isDisabled, onError)
        }.getOrElse {
            logger.e(it, "extractActionAttrs: $action")
            null
        }
    }
}