package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.Rule
import tornaco.apps.shortx.core.proto.RuleList
import tornaco.apps.shortx.core.util.Logger

private const val WRITE_DELAY = 3000L

class RuleSettingsStore(private val scope: CoroutineScope) {
    private val logger = Logger("RuleSettingsStore")
    private val storeFile by lazy { Dirs.dataStoreFile("rules") }

    private val rulesCache: MutableList<Rule> = mutableListOf()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addRule(rule: Rule) {
        rulesCache.add(rule)
        postWrite()
    }

    fun getAllRules(): List<Rule> {
        return rulesCache.toList()
    }

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

    private suspend fun write() = withContext(Dispatchers.IO) {
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