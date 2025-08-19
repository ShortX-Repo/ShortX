package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.rule.RuleSetList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseRuleSetsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("RuleSetsStore")

    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val ruleSetsCache: MutableList<RuleSet> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addRuleSets(ruleSets: List<RuleSet>, write: Boolean = true) {
        synchronized(ruleSetsCache) {
            ruleSets.forEach { pkgSet ->
                ruleSetsCache.removeIf { it.id == pkgSet.id }
                ruleSetsCache.add(pkgSet)
            }
            if (write) postWrite()
        }
    }

    fun addRuleSet(set: RuleSet) {
        synchronized(ruleSetsCache) {
            ruleSetsCache.removeIf { it.id == set.id }
            ruleSetsCache.add(set)
            postWrite()
        }
    }

    fun deleteRuleSet(id: String) {
        synchronized(ruleSetsCache) {
            ruleSetsCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getRuleSet(id: String): RuleSet? {
        synchronized(ruleSetsCache) {
            return ruleSetsCache.firstOrNull { it.id == id }
        }
    }

    fun getAllRuleSets(): List<RuleSet> {
        synchronized(ruleSetsCache) {
            return ruleSetsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(ruleSetsCache) {
            kotlin.runCatching {
                val pkgSetList = atomicStoreFile.read {
                    RuleSetList.parseDelimitedFrom(it)
                }
                pkgSetList.run {
                    ruleSetsCache.clear()
                    ruleSetsCache.addAll(this.ruleSetList)
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
        synchronized(ruleSetsCache) {
            logger.d("write.")
            val pkgSetList = RuleSetList.newBuilder().addAllRuleSet(ruleSetsCache).build()
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