package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.pkgset.PkgSetList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BasePkgSetsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("PkgSetStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val pkgSetsCache: MutableList<PkgSet> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllPkgSets(pkgSets: List<PkgSet>, write: Boolean = true) {
        synchronized(pkgSetsCache) {
            pkgSets.forEach { pkgSet ->
                pkgSetsCache.removeIf { it.label == pkgSet.label }
                pkgSetsCache.add(pkgSet)
            }
            if (write) postWrite()
        }
    }

    fun addPkgSet(pkgSet: PkgSet) {
        synchronized(pkgSetsCache) {
            pkgSetsCache.removeIf { it.label == pkgSet.label }
            pkgSetsCache.add(pkgSet)
            postWrite()
        }
    }

    fun deletePkgSet(pkgSetLabel: String) {
        synchronized(pkgSetsCache) {
            pkgSetsCache.removeIf { it.label == pkgSetLabel }
            postWrite()
        }
    }

    fun getPkgSet(label: String): PkgSet? {
        synchronized(pkgSetsCache) {
            return pkgSetsCache.firstOrNull { it.label == label }
        }
    }

    fun getAllPkgSets(): List<PkgSet> {
        synchronized(pkgSetsCache) {
            return pkgSetsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(pkgSetsCache) {
            kotlin.runCatching {
                val pkgSetList = atomicStoreFile.read {
                    PkgSetList.parseDelimitedFrom(it)
                }
                pkgSetList.run {
                    pkgSetsCache.clear()
                    pkgSetsCache.addAll(this.pkgSetsList)
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
        logger.d("write.")
        synchronized(pkgSetsCache) {
            val pkgSetList = PkgSetList.newBuilder().apply {
                addAllPkgSets(pkgSetsCache)
            }.build()
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