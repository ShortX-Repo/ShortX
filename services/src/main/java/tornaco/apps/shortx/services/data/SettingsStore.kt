package tornaco.apps.shortx.services.data

import kotlinx.coroutines.*
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.mapToString
import tornaco.apps.shortx.services.proto.Settings
import tornaco.apps.shortx.services.proto.StringMap

private const val WRITE_DELAY = 3000L

class SettingsStore(private val name: String, private val scope: CoroutineScope) {
    private val logger = Logger("${SettingsStore::class.java.simpleName}-$name")
    private val storeFile by lazy { Dirs.dataStoreFile(name) }

    private val stringCache = mutableMapOf<String, String?>()
    private val boolCache = mutableMapOf<String, Boolean?>()
    private val intCache = mutableMapOf<String, Int?>()
    private val stringMapCache = mutableMapOf<String, Map<String, String>>()

    private var job: Job? = null

    fun dump() {
        logger.i("-----------------------")
        logger.i("stringCache")
        logger.i(stringCache.mapToString())
        logger.i("boolCache")
        logger.i(boolCache.mapToString())
        logger.i("intCache")
        logger.i(intCache.mapToString())
        logger.i("stringMapCache")
        logger.i(stringMapCache.mapToString())
        logger.i("-----------------------")
    }

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
                stringCache.putAll(stringSettingsMap)
                boolCache.putAll(boolSettingsMap)
                intCache.putAll(intSettingsMap)

                stringMapSettingsMap.forEach {
                    stringMapCache[it.key] = it.value.dataMap
                }
            }
        }.onFailure {
            logger.e(it, "read error")
        }
    }


    fun getString(key: String, defaultValue: String? = null): String? {
        return stringCache[key] ?: defaultValue
    }

    fun getBool(key: String, defaultValue: Boolean = false): Boolean {
        return boolCache[key] ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int? = null): Int? {
        return intCache[key] ?: defaultValue
    }

    fun putString(key: String, value: String?) {
        stringCache[key] = value
        postWrite()
    }

    fun putBool(key: String, value: Boolean) {
        boolCache[key] = value
        postWrite()
    }

    fun putStringMap(key: String, value: Map<String, String>) {
        stringMapCache[key] = value
        postWrite()
    }

    fun getStringMap(
        key: String,
        defaultValue: Map<String, String> = emptyMap(),
    ): Map<String, String> {
        return stringMapCache[key] ?: defaultValue
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
            putAllStringSettings(stringCache)
            putAllBoolSettings(boolCache)
            putAllIntSettings(intCache)

            stringMapCache.forEach {
                putStringMapSettings(it.key, StringMap.newBuilder().putAllData(it.value).build())
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