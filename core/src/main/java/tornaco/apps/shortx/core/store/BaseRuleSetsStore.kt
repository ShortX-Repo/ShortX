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
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseRuleSetsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("RuleSetsStore")

    private val pkgSetsCache: MutableList<RuleSet> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllRuleSets(ruleSets: List<RuleSet>, write: Boolean = true) {
        ruleSets.forEach { pkgSet ->
            pkgSetsCache.removeIf { it.id == pkgSet.id }
            pkgSetsCache.add(pkgSet)
        }
        if (write) postWrite()
    }

    fun addRuleSet(set: RuleSet) {
        pkgSetsCache.removeIf { it.id == set.id }
        pkgSetsCache.add(set)
        postWrite()
    }

    fun deleteRuleSet(id: String) {
        pkgSetsCache.removeIf { it.id == id }
        postWrite()
    }

    fun getRuleSet(id: String): RuleSet? {
        return pkgSetsCache.firstOrNull { it.id == id }
    }

    fun getAllRuleSets(): List<RuleSet> {
        return pkgSetsCache.toList()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val pkgSetList = storeFile.inputStream().use {
                RuleSetList.parseDelimitedFrom(it)
            }
            pkgSetList.run {
                pkgSetsCache.clear()
                pkgSetsCache.addAll(this.ruleSetList)
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
        val pkgSetList = RuleSetList.newBuilder().apply {
            addAllRuleSets(pkgSetsCache)
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