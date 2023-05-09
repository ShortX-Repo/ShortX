package tornaco.apps.shortx.core.store

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionList
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseDirectActionSettingsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("DirectActionSettingsStore")

    private val dasCache: MutableList<DirectAction> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addDirectAction(da: DirectAction) {
        dasCache.removeIf { it.id == da.id }
        dasCache.add(da)
        postWrite()
    }

    fun addDirectActions(das: List<DirectAction>, write: Boolean = true) {
        das.forEach { da ->
            dasCache.removeIf { it.id == da.id }
            dasCache.add(da)
        }
        if (write) postWrite()
    }

    fun deleteDirectAction(daId: String) {
        dasCache.removeIf { it.id == daId }
        postWrite()
    }

    fun getDirectActionById(id: String): DirectAction? {
        return dasCache.firstOrNull { it.id == id }
    }

    fun getAllDirectActions(): List<DirectAction> {
        return dasCache.toList()
    }

    fun getDirectActionCount() = dasCache.size

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val daList = storeFile.inputStream().use {
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

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        val daList = DirectActionList.newBuilder().apply {
            addAllDirectActions(dasCache)
        }.build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                daList.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}