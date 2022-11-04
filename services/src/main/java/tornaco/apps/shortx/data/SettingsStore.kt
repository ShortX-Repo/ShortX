package tornaco.apps.shortx.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.proto.Settings

private const val WRITE_DELAY = 3000L

class SettingsStore(private val name: String, private val scope: CoroutineScope) {
    private val logger = Logger("${SettingsStore::class.java.simpleName}-$name")
    private val storeFile by lazy { Dirs.dataStoreFile(name) }

    private val stringMap = mutableMapOf<String, String?>()
    private val intMap = mutableMapOf<String, Int>()

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
                Settings.parseDelimitedFrom(it)
            }
            settings.run {
                stringMap.putAll(stringSettingsMap)
                intMap.putAll(intSettingsMap)
            }
        }.onFailure {
            logger.e(it, "read error")
        }
    }


    fun getString(key: String, defaultValue: String? = null): String? {
        return stringMap.getOrDefault(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int? = null): Int? {
        return intMap.getOrDefault(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        stringMap[key] = value
        postWrite()
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
        val settings = Settings.newBuilder().apply {
            putAllStringSettings(stringMap)
            putAllIntSettings(intMap)
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