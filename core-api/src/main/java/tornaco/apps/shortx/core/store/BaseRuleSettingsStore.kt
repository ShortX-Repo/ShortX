package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseRuleSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("RuleSettingsStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val rulesCache: MutableList<Rule> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addRules(rules: List<Rule>, write: Boolean = true) {
        synchronized(rulesCache) {
            rules.forEach { rule ->
                rulesCache.removeIf { it.id == rule.id }
                rulesCache.add(rule)
            }
            if (write) postWrite()
        }
    }

    fun addRule(rule: Rule) {
        synchronized(rulesCache) {
            require(rule.extrasList.isEmpty()) {
                "Rule extra must be cleared."
            }
            rulesCache.removeIf { it.id == rule.id }
            rulesCache.add(rule)
            postWrite()
        }
    }

    fun deleteRule(ruleId: String): List<Rule> {
        synchronized(rulesCache) {
            val toBeDeleted = rulesCache.filter { it.id == ruleId }
            rulesCache.removeAll(toBeDeleted)
            postWrite()
            return toBeDeleted
        }
    }

    fun getRuleById(id: String): Rule? {
        synchronized(rulesCache) {
            return rulesCache.firstOrNull { it.id == id }
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        synchronized(rulesCache) {
            rulesCache.replaceAll {
                if (it.id == ruleId) {
                    Rule.newBuilder().mergeFrom(it).setIsEnabled(enable).build()
                } else {
                    it
                }
            }
            postWrite()
        }
    }

    fun getAllRules(): List<Rule> {
        synchronized(rulesCache) {
            return rulesCache.toList()
        }
    }

    fun getRuleCount() = synchronized(rulesCache) {
        rulesCache.size
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(rulesCache) {
            kotlin.runCatching {
                val ruleList = atomicStoreFile.read {
                    RuleList.parseDelimitedFrom(it)
                }
                ruleList.run {
                    rulesCache.clear()
                    rulesCache.addAll(this.rulesList)
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
        synchronized(rulesCache) {
            logger.d("write.")
            val ruleList = RuleList.newBuilder().apply {
                addAllRules(rulesCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    ruleList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}