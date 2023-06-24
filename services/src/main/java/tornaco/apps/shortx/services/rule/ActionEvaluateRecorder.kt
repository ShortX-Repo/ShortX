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
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Success -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("Success")
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
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