package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseDirectActionSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("DirectActionSettingsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val dasCache: MutableList<DirectAction> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addDirectAction(da: DirectAction) {
        synchronized(dasCache) {
            require(da.extrasList.isEmpty()) {
                "DA extra must be cleared."
            }
            dasCache.removeIf { it.id == da.id }
            dasCache.add(da)
            postWrite()
        }
    }

    fun addDirectActions(das: List<DirectAction>, write: Boolean = true) {
        synchronized(dasCache) {
            das.forEach { da ->
                dasCache.removeIf { it.id == da.id }
                dasCache.add(da)
            }
            if (write) postWrite()
        }
    }

    fun deleteDirectAction(daId: String) {
        synchronized(dasCache) {
            dasCache.removeIf { it.id == daId }
            postWrite()
        }
    }

    fun getDirectActionById(id: String): DirectAction? {
        synchronized(dasCache) {
            return dasCache.firstOrNull { it.id == id }
        }
    }

    fun getAllDirectActions(): List<DirectAction> {
        synchronized(dasCache) {
            return dasCache.toList()
        }
    }

    fun getDirectActionCount() = synchronized(dasCache) {
        dasCache.size
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        synchronized(dasCache) {
            if (!atomicStoreFile.exists()) {
                logger.d("skip read no-exists file: $atomicStoreFile")
                return@withContext
            }
            kotlin.runCatching {
                val daList = atomicStoreFile.read {
                    DirectActionList.parseDelimitedFrom(it)
                }
                daList.run {
                    dasCache.clear()
                    dasCache.addAll(this.directActionsList)
                }
            }.onFailure {
                logger.e(it, "read error")
            }
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
        synchronized(dasCache) {
            logger.d("write.")
            val daList = DirectActionList.newBuilder().apply {
                addAllDirectActions(dasCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    daList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}