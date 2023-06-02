package tornaco.apps.shortx.services.rule

import android.util.Log
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord


fun ActionEvaluator.EvaluateResult.toActionEvaluateRecord(): ActionEvaluateRecord {
    return when (this) {
        ActionEvaluator.EvaluateResult.Break -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage("Break")
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Fail -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage(Log.getStackTraceString(this.error))
                .setActionClassName(actionProtoType.name)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Success -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("Success")
                .setActionClassName(actionProtoType.name)
                .setTimeStamp(System.currentTimeMillis()).build()
        }
    }
}

class ActionEvaluateRecorder {
    private val _actionEvaluateRecords = mutableListOf<ActionEvaluateRecord>()

    val actionEvaluateRecords get() = _actionEvaluateRecords.toList()

    fun add(record: ActionEvaluateRecord) {
        _actionEvaluateRecords.add(record)
    }

    fun clear() = _actionEvaluateRecords.clear()
}