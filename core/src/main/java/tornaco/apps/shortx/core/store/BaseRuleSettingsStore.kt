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
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseRuleSettingsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("RuleSettingsStore")

    private val rulesCache: MutableList<Rule> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    suspend fun read() = readInternal()

    fun addRules(rules: List<Rule>, write: Boolean = true) {
        rules.forEach { rule ->
            rulesCache.removeIf { it.id == rule.id }
            rulesCache.add(rule)
        }
        if (write) postWrite()
    }

    fun addRule(rule: Rule) {
        require(rule.extrasList.isEmpty()) {
            "Rule extra must be cleared."
        }
        rulesCache.removeIf { it.id == rule.id }
        rulesCache.add(rule)
        postWrite()
    }

    fun deleteRule(ruleId: String): List<Rule> {
        val toBeDeleted = rulesCache.filter { it.id == ruleId }
        rulesCache.removeAll(toBeDeleted)
        postWrite()
        return toBeDeleted
    }

    fun getRuleById(id: String): Rule? {
        return rulesCache.firstOrNull { it.id == id }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        rulesCache.replaceAll {
            if (it.id == ruleId) {
                Rule.newBuilder().mergeFrom(it).setIsEnabled(enable).build()
            } else {
                it
            }
        }
        postWrite()
    }

    fun getAllRules(): List<Rule> {
        return rulesCache.toList()
    }

    fun getRuleCount() = rulesCache.size

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val ruleList = storeFile.inputStream().use {
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

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            delay(WRITE_DELAY)
            write()
        }
    }

    suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        val ruleList = RuleList.newBuilder().apply {
            addAllRules(rulesCache)
        }.build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                ruleList.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}