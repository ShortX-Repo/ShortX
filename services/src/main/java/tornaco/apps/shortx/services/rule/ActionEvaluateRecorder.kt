package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord


fun ActionEvaluator.EvaluateResult.toActionEvaluateRecord(): ActionEvaluateRecord? {
    return when (this) {
        is ActionEvaluator.EvaluateResult.Break -> {
            ActionEvaluateRecord.newBuilder()
                .setActionId(this.actionId)
                .setIsSuccess(true)
                .setMessage("[break!!!]")
                .setContext(this.evaluateContext)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Fail -> {
            ActionEvaluateRecord.newBuilder()
                .setActionId(this.actionId)
                .setIsSuccess(false)
                .setMessage(this.error.stackTraceToString())
                .setContext(this.evaluateContext)
                .setAction(action)
                .setTimeStamp(System.currentTimeMillis()).build()
        }

        is ActionEvaluator.EvaluateResult.Success -> {
            ActionEvaluateRecord.newBuilder()
                .setActionId(this.actionId)
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


private const val MAX_ENTRY_SIZE = 500

class ActionEvaluateRecorder {
    private val _actionEvaluateRecords = mutableListOf<ActionEvaluateRecord>()
    val actionEvaluateRecords get() = _actionEvaluateRecords.toList()

    private val _evaluatingActions = mutableListOf<String>()
    val evaluatingActions get() = _evaluatingActions.toList()

    @Synchronized
    fun addEvaluating(actionId: String) {
        _evaluatingActions.add(actionId)
    }

    @Synchronized
    fun removeEvaluating(actionId: String) {
        _evaluatingActions.remove(actionId)
    }

    @Synchronized
    fun addResult(record: ActionEvaluateRecord) {
        _actionEvaluateRecords.add(record)
        if (_actionEvaluateRecords.size > MAX_ENTRY_SIZE) {
            _actionEvaluateRecords.removeFirst()
        }
    }

    @Synchronized
    fun clear() = _actionEvaluateRecords.clear()
}