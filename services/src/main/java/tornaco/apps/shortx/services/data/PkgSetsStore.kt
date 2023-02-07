package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.pkgset.PkgSetList
import tornaco.apps.shortx.core.util.Logger

private const val WRITE_DELAY = 3000L

class PkgSetsStore(private val scope: CoroutineScope) {
    private val logger = Logger("PkgSetStore")
    private val storeFile by lazy { Dirs.dataStoreFile("pkgsets") }

    private val pkgSetsCache: MutableList<PkgSet> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addPkgSet(pkgSet: PkgSet) {
        pkgSetsCache.removeIf { it.id == pkgSet.id }
        pkgSetsCache.add(pkgSet)
        postWrite()
    }

    fun deletePkgSet(pkgSetId: String) {
        pkgSetsCache.removeIf { it.id == pkgSetId }
        postWrite()
    }

    fun getPkgSetById(id: String): PkgSet? {
        return pkgSetsCache.firstOrNull { it.id == id }
    }

    fun getAllPkgSets(): List<PkgSet> {
        return pkgSetsCache.toList()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
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

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    private suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
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