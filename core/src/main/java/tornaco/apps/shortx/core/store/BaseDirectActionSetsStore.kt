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
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseDirectActionSetsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("DirectActionSetsStore")

    private val pkgSetsCache: MutableList<DirectActionSet> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    @Synchronized
    fun addDirectActionSets(directActionSets: List<DirectActionSet>, write: Boolean = true) {
        directActionSets.forEach { pkgSet ->
            pkgSetsCache.removeIf { it.id == pkgSet.id }
            pkgSetsCache.add(pkgSet)
        }
        if (write) postWrite()
    }

    @Synchronized
    fun addDirectActionSet(set: DirectActionSet) {
        pkgSetsCache.removeIf { it.id == set.id }
        pkgSetsCache.add(set)
        postWrite()
    }

    @Synchronized
    fun deleteDirectActionSet(id: String) {
        pkgSetsCache.removeIf { it.id == id }
        postWrite()
    }

    @Synchronized
    fun getDirectActionSet(id: String): DirectActionSet? {
        return pkgSetsCache.firstOrNull { it.id == id }
    }

    fun getAllDirectActionSets(): List<DirectActionSet> {
        return pkgSetsCache.toList()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val pkgSetList = storeFile.inputStream().use {
                DirectActionSetList.parseDelimitedFrom(it)
            }
            pkgSetList.run {
                pkgSetsCache.clear()
                pkgSetsCache.addAll(this.directActionSetList)
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
        val pkgSetList =
            DirectActionSetList.newBuilder().addAllDirectActionSet(pkgSetsCache).build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                pkgSetList.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}