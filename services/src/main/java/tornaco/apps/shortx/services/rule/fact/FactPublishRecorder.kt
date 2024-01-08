package tornaco.apps.shortx.services.rule.fact

import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_

fun ProtoMessage.toFactPublishRecord(publishTimeMillis: Long): FactPublishRecord =
    FactPublishRecord.newBuilder()
        .setFact(this.pack_())
        .setTimeStamp(publishTimeMillis).build()

private const val MAX_ENTRY_SIZE = 200

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