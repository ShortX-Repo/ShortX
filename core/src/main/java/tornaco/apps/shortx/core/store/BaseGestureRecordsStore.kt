package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.GestureRecordList
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseGestureRecordsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("GestureRecordsStore")

    private val gestureRecordsCache: MutableList<GestureRecord> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllGestureRecords(records: List<GestureRecord>, write: Boolean = true) {
        records.forEach { record ->
            gestureRecordsCache.removeIf { it.id == record.id }
            gestureRecordsCache.add(record)
        }
        if (write) postWrite()
    }

    fun addGestureRecord(record: GestureRecord) {
        logger.d("addGestureRecord: ${record.id}")
        gestureRecordsCache.removeIf { it.id == record.id }
        gestureRecordsCache.add(record)
        postWrite()
    }

    fun deleteGestureRecord(id: String) {
        gestureRecordsCache.removeIf { it.id == id }
        postWrite()
    }

    fun getGestureRecord(id: String): GestureRecord? {
        return gestureRecordsCache.firstOrNull { it.id == id }
    }

    fun getAllGestureRecords(): List<GestureRecord> {
        return gestureRecordsCache.toList()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val recordList = storeFile.inputStream().use {
                GestureRecordList.parseDelimitedFrom(it)
            }
            gestureRecordsCache.clear()
            gestureRecordsCache.addAll(recordList.recordsList)
        }.onFailure {
            logger.e(it, "read error")
        }
    }

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        val recordList = GestureRecordList.newBuilder().apply {
            addAllRecords(gestureRecordsCache)
        }.build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                recordList.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}