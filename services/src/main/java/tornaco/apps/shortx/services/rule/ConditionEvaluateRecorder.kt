package tornaco.apps.shortx.services.rule

import android.util.Log
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord


fun ConditionEvaluator.EvaluateResult.toConditionEvaluateRecord(): ConditionEvaluateRecord {
    return when (this) {
        ConditionEvaluator.EvaluateResult.Break -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage("Break")
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ConditionEvaluator.EvaluateResult.Fail -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage(Log.getStackTraceString(this.error))
                .setConditionClassName(protoType.name)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ConditionEvaluator.EvaluateResult.Success -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setIsMatch(match)
                .setMessage("Success")
                .setConditionClassName(protoType.name)
                .setTimeStamp(System.currentTimeMillis()).build()
        }
    }
}

private const val MAX_ENTRY_SIZE = 200

class ConditionEvaluateRecorder {
    private val _conditionEvaluateRecords = mutableListOf<ConditionEvaluateRecord>()

    val conditionEvaluateRecords get() = _conditionEvaluateRecords.toList()

    fun add(record: ConditionEvaluateRecord) {
        _conditionEvaluateRecords.add(record)
        if (_conditionEvaluateRecords.size > MAX_ENTRY_SIZE) {
            _conditionEvaluateRecords.removeFirst()
        }
    }

    fun clear() = _conditionEvaluateRecords.clear()
}