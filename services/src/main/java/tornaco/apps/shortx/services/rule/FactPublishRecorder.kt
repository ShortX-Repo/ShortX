package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.rule.ProtoMessage

fun ProtoMessage.toFactPublishRecord(): FactPublishRecord =
    FactPublishRecord.newBuilder().setFactClassName(this::class.java.name)
        .setTimeStamp(System.currentTimeMillis()).build()

private const val MAX_ENTRY_SIZE = 100

class FactPublishRecorder {
    private val _records = mutableListOf<FactPublishRecord>()
    val records get() = _records.toList()

    fun add(record: FactPublishRecord) {
        _records.add(record)
        if (_records.size > MAX_ENTRY_SIZE) {
            _records.removeFirst()
        }
    }

    fun clear() {
        _records.clear()
    }
}