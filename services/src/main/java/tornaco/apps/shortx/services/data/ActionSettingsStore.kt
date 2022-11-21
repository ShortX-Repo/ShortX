package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.action.Action
import tornaco.apps.shortx.core.action.NoAction
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.proto.ActionSetting
import tornaco.apps.shortx.services.proto.ActionSettings
import tornaco.apps.shortx.services.proto.StartActivity
import tornaco.apps.shortx.services.proto.TakeScreenshot

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

    operator fun get(key: String): List<Action> {
        return dataCache[key]?.let { actionSetting ->
            actionSetting.payloadList.map { any ->
                if (any.`is`(TakeScreenshot::class.java)) {
                    tornaco.apps.shortx.core.action.TakeScreenshot()
                } else if (any.`is`(StartActivity::class.java)) {
                    val startActivity = any.unpack(StartActivity::class.java)
                    tornaco.apps.shortx.core.action.StartActivity(startActivity.componentNameAsString)
                } else {
                    NoAction()
                }
            }
        } ?: emptyList()
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