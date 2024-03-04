package tornaco.apps.shortx.core.store

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.mapToString
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseSettingsStore(
    val context: Context,
    name: String,
    storeFile: File,
    private val scope: CoroutineScope,
) {
    private val logger = Logger("${BaseSettingsStore::class.java.simpleName}-$name")

    private val atomicStoreFile = AtomicFileCompat(storeFile)

    private val stringCache = mutableMapOf<String, String?>()
    private val boolCache = mutableMapOf<String, Boolean?>()
    private val intCache = mutableMapOf<String, Int?>()
    private val floatCache = mutableMapOf<String, Float?>()
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
        logger.i("floatCache")
        logger.i(floatCache.mapToString())
        logger.i("stringMapCache")
        logger.i(stringMapCache.mapToString())
        logger.i("-----------------------")
    }

    fun readBlocking() = runBlocking {
        readInternal()
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        kotlin.runCatching {
            val settings = atomicStoreFile.read {
                tornaco.apps.shortx.services.proto.Settings.parseDelimitedFrom(it)
            }
            settings.run {
                stringCache.putAll(stringSettingsMap)
                boolCache.putAll(boolSettingsMap)
                intCache.putAll(intSettingsMap)
                floatCache.putAll(floatSettingsMap)

                stringMapSettingsMap.forEach {
                    stringMapCache[it.key] = it.value.dataMap
                }
            }
        }.onFailure {
            logger.e(it, "read error")
        }
    }

    fun clear() {
        stringCache.clear()
        boolCache.clear()
        intCache.clear()
        floatCache.clear()
        stringMapCache.clear()
    }

    fun getAllString(): Map<String, String?> {
        return stringCache.toMap()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return stringCache[key] ?: defaultValue
    }

    fun getBool(key: String, defaultValue: Boolean): Boolean {
        return boolCache[key] ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return intCache[key] ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        intCache[key] = value
        postWrite()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return floatCache[key] ?: defaultValue
    }

    fun putFloat(key: String, value: Float) {
        floatCache[key] = value
        postWrite()
    }

    fun putString(key: String, value: String?) {
        stringCache[key] = value
        postWrite()
    }

    fun removeString(key: String) {
        stringCache.remove(key)
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
        val settings = tornaco.apps.shortx.services.proto.Settings.newBuilder().apply {
            putAllStringSettings(stringCache)
            putAllBoolSettings(boolCache)
            putAllIntSettings(intCache)
            putAllFloatSettings(floatCache)

            stringMapCache.forEach {
                putStringMapSettings(
                    it.key,
                    tornaco.apps.shortx.services.proto.StringMap.newBuilder().putAllData(it.value)
                        .build()
                )
            }
        }.build()
        kotlin.runCatching {
            atomicStoreFile.writeInlined {
                settings.writeDelimitedTo(it)
                logger.d("write to: $atomicStoreFile")
            }
        }.onFailure {
            logger.e(it, "write error")
        }
    }
}