package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord


fun ActionEvaluator.EvaluateResult.toActionEvaluateRecord(): ActionEvaluateRecord? {
    return when (this) {
        is ActionEvaluator.EvaluateResult.Break -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("[break!!!]")
                .setContext(this.evaluateContext)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Fail -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(false)
                .setMessage(this.error.stackTraceToString())
                .setContext(this.evaluateContext)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Success -> {
            ActionEvaluateRecord.newBuilder()
                .setIsSuccess(true)
                .setMessage("Success")
                .setContext(this.evaluateContext)
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