package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.da.DirectActionSetList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseDirectActionSetsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("DirectActionSetsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val pkgSetsCache: MutableList<DirectActionSet> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addDirectActionSets(directActionSets: List<DirectActionSet>, write: Boolean = true) {
        synchronized(pkgSetsCache) {
            directActionSets.forEach { pkgSet ->
                pkgSetsCache.removeIf { it.id == pkgSet.id }
                pkgSetsCache.add(pkgSet)
            }
            if (write) postWrite()
        }
    }

    fun addDirectActionSet(set: DirectActionSet) {
        synchronized(pkgSetsCache) {
            pkgSetsCache.removeIf { it.id == set.id }
            pkgSetsCache.add(set)
            postWrite()
        }
    }

    fun deleteDirectActionSet(id: String) {
        synchronized(pkgSetsCache) {
            pkgSetsCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getDirectActionSet(id: String): DirectActionSet? {
        synchronized(pkgSetsCache) {
            return pkgSetsCache.firstOrNull { it.id == id }
        }
    }

    fun getAllDirectActionSets(): List<DirectActionSet> {
        synchronized(pkgSetsCache) {
            return pkgSetsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        kotlin.runCatching {
            synchronized(pkgSetsCache) {
                val pkgSetList = atomicStoreFile.read {
                    DirectActionSetList.parseDelimitedFrom(it)
                }
                pkgSetList.run {
                    pkgSetsCache.clear()
                    pkgSetsCache.addAll(this.directActionSetList)
                }
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
        synchronized(pkgSetsCache) {
            logger.d("write.")
            val pkgSetList =
                DirectActionSetList.newBuilder().addAllDirectActionSet(pkgSetsCache).build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    pkgSetList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}