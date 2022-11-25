package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.proto.ActionSetting
import tornaco.apps.shortx.core.proto.ActionSettings
import tornaco.apps.shortx.core.util.Logger

private const val WRITE_DELAY = 3000L
private const val STORE_NAME = "ActionSettings"

class ActionSettingsStore(private val scope: CoroutineScope) {
    private val logger = Logger(STORE_NAME)
    private val storeFile by lazy { Dirs.dataStoreFile(STORE_NAME) }

    private val dataCache = mutableMapOf<String, ActionSetting>()

    private var job: Job? = null

    fun readBlocking() = runBlocking {
        readInternal()
    }


    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val settings = storeFile.inputStream().use {
                ActionSettings.parseDelimitedFrom(it)
            }
            settings.run {
                dataMap.forEach {
                    data[it.key] = it.value
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

    private suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        val settings = ActionSettings.newBuilder().apply {
            dataMap.forEach { _ ->
                putAllData(dataCache)
            }
        }.build()
        kotlin.runCatching {
            storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                settings.writeDelimitedTo(it)
                logger.d("write to: $storeFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}