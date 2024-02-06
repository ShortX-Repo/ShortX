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
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BasePkgSetsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("PkgSetStore")

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
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        synchronized(pkgSetsCache) {
            kotlin.runCatching {
                val pkgSetList = storeFile.inputStream().use {
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
                storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                    pkgSetList.writeDelimitedTo(it)
                    logger.d("write to: $storeFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}