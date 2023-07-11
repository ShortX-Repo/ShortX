package tornaco.apps.shortx.services.rule

import android.util.Log
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.util.fallbackOnEmpty


fun ActionEvaluator.EvaluateResult.toActionEvaluateRecord(contextData: ContextData): ActionEvaluateRecord? {
    val srcTitle = contextData.get(ContextDataMapping.Context.contextSrcTitle.name)
    val srcEvent: ContextSrcEvent =
        (contextData.get(ContextDataMapping.Context.contextSrcEvent.name) as ContextSrcEvent?)
            ?: ContextSrcEvent.UNRECOGNIZED
    return when (this) {
        is ActionEvaluator.EvaluateResult.Break -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("[break!!!]")
                .setSrcTitle(srcTitle?.toString().fallbackOnEmpty(""))
                .setSrcEvent(srcEvent)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Fail -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage(Log.getStackTraceString(this.error))
                .setSrcTitle(srcTitle?.toString().fallbackOnEmpty(""))
                .setSrcEvent(srcEvent)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Success -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("Success")
                .setSrcTitle(srcTitle?.toString().fallbackOnEmpty(""))
                .setSrcEvent(srcEvent)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        else -> {
            null
        }
    }
}


private const val MAX_ENTRY_SIZE = 200

class ActionEvaluateRecorder {
    private val _actionEvaluateRecords = mutableListOf<ActionEvaluateRecord>()

    val actionEvaluateRecords get() = _actionEvaluateRecords.toList()

    fun add(record: ActionEvaluateRecord) {
        _actionEvaluateRecords.add(record)
        if (_actionEvaluateRecords.size > MAX_ENTRY_SIZE) {
            _actionEvaluateRecords.removeFirst()
        }
    }

    fun clear() = _actionEvaluateRecords.clear()
}