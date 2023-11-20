package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord


fun ConditionEvaluator.EvaluateResult.toConditionEvaluateRecord(): ConditionEvaluateRecord {
    return when (this) {
        is ConditionEvaluator.EvaluateResult.Break -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage("Break")
                .setContext(this.evaluateContext)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ConditionEvaluator.EvaluateResult.Fail -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setContext(this.evaluateContext)
                .setMessage(this.error.stackTraceToString())
                .setCondition(condition)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ConditionEvaluator.EvaluateResult.Success -> {
            ConditionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setContext(this.evaluateContext)
                .setIsMatch(match)
                .setMessage(reason)
                .setCondition(condition)
                .setTimeStamp(System.currentTimeMillis()).build()
        }
    }
}

private const val MAX_ENTRY_SIZE = 200

class ConditionEvaluateRecorder {
    private val _conditionEvaluateRecords = mutableListOf<ConditionEvaluateRecord>()

    val conditionEvaluateRecords get() = _conditionEvaluateRecords.toList()

    @Synchronized
    fun add(record: ConditionEvaluateRecord) {
        _conditionEvaluateRecords.add(record)
        if (_conditionEvaluateRecords.size > MAX_ENTRY_SIZE) {
            _conditionEvaluateRecords.removeFirst()
        }
    }

    @Synchronized
    fun clear() = _conditionEvaluateRecords.clear()
}