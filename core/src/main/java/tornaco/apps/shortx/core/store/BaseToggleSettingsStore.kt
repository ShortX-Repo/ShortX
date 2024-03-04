package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.proto.toggles.ToggleList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseToggleSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("ToggleSettingsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val togglesCache: MutableList<Toggle> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addToggle(da: Toggle) {
        synchronized(togglesCache) {
            require(da.extrasList.isEmpty()) {
                "Toggle extra must be cleared."
            }
            togglesCache.removeIf { it.id == da.id }
            togglesCache.add(da)
            postWrite()
        }
    }

    fun addToggles(das: List<Toggle>, write: Boolean = true) {
        synchronized(togglesCache) {
            das.forEach { da ->
                togglesCache.removeIf { it.id == da.id }
                togglesCache.add(da)
            }
            if (write) postWrite()
        }
    }

    fun deleteToggle(id: String) {
        synchronized(togglesCache) {
            togglesCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getToggleById(id: String): Toggle? {
        synchronized(togglesCache) {
            return togglesCache.firstOrNull { it.id == id }
        }
    }

    fun getAllToggles(): List<Toggle> {
        synchronized(togglesCache) {
            return togglesCache.toList()
        }
    }

    fun getToggleCount() = synchronized(togglesCache) {
        togglesCache.size
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(togglesCache) {
            kotlin.runCatching {
                val daList = atomicStoreFile.read {
                    ToggleList.parseDelimitedFrom(it)
                }
                daList.run {
                    togglesCache.clear()
                    togglesCache.addAll(this.toggleList)
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
        synchronized(togglesCache) {
            logger.d("write.")
            val toggleList = ToggleList.newBuilder().apply {
                addAllToggle(togglesCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    toggleList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}