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
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseGestureRecordsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("GestureRecordsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val gestureRecordsCache: MutableList<GestureRecord> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllGestureRecords(records: List<GestureRecord>, write: Boolean = true) {
        synchronized(gestureRecordsCache) {
            records.forEach { record ->
                gestureRecordsCache.removeIf { it.id == record.id }
                gestureRecordsCache.add(record)
            }
            if (write) postWrite()
        }
    }

    fun addGestureRecord(record: GestureRecord) {
        synchronized(gestureRecordsCache) {
            logger.d("addGestureRecord: ${record.id}")
            gestureRecordsCache.removeIf { it.id == record.id }
            gestureRecordsCache.add(record)
            postWrite()
        }
    }

    fun deleteGestureRecord(id: String) {
        synchronized(gestureRecordsCache) {
            gestureRecordsCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getGestureRecord(id: String): GestureRecord? {
        synchronized(gestureRecordsCache) {
            return gestureRecordsCache.firstOrNull { it.id == id }
        }
    }

    fun getAllGestureRecords(): List<GestureRecord> {
        synchronized(gestureRecordsCache) {
            return gestureRecordsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        kotlin.runCatching {
            synchronized(gestureRecordsCache) {
                val recordList = atomicStoreFile.read {
                    GestureRecordList.parseDelimitedFrom(it)
                }
                gestureRecordsCache.clear()
                gestureRecordsCache.addAll(recordList.recordsList)
            }
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
        synchronized(gestureRecordsCache) {
            logger.d("write.")
            val recordList = GestureRecordList.newBuilder().apply {
                addAllRecords(gestureRecordsCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    recordList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}