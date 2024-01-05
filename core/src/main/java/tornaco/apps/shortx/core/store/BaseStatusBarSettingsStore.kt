package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.StatusBarTileSettingList
import tornaco.apps.shortx.core.util.Logger
import java.io.File

open class BaseStatusBarSettingsStore(
    private val scope: CoroutineScope,
    private val storeFile: File
) {
    private val logger = Logger("StatusBarSettingsStore")

    private val tileSettingsCache: MutableList<StatusBarTileSetting> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllSettings(records: List<StatusBarTileSetting>, write: Boolean = true) {
        records.forEach { record ->
            synchronized(tileSettingsCache) {
                tileSettingsCache.removeIf { it.tile == record.tile }
                tileSettingsCache.add(record)
            }
        }
        if (write) postWrite()
    }

    fun addSetting(setting: StatusBarTileSetting) {
        synchronized(tileSettingsCache) {
            tileSettingsCache.removeIf { setting.tile == it.tile }
            tileSettingsCache.add(setting)
        }
        postWrite()
    }

    fun deleteSetting(tile: StatusBarTile) {
        synchronized(tileSettingsCache) {
            tileSettingsCache.removeIf { it.tile == tile }
        }
        postWrite()
    }

    fun getSettingByTile(tile: StatusBarTile): StatusBarTileSetting? {
        synchronized(tileSettingsCache) {
            return tileSettingsCache.firstOrNull { it.tile == tile }
        }
    }

    fun getAllSettings(): List<StatusBarTileSetting> {
        synchronized(tileSettingsCache) {
            return tileSettingsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            val tileSettingList = storeFile.inputStream().use {
                StatusBarTileSettingList.parseDelimitedFrom(it)
            }
            synchronized(tileSettingsCache) {
                tileSettingsCache.clear()
                tileSettingsCache.addAll(tileSettingList.settingsList)
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

        synchronized(tileSettingsCache) {
            val statusBarTileSettingList = StatusBarTileSettingList.newBuilder().apply {
                addAllSettings(tileSettingsCache)
            }.build()
            kotlin.runCatching {
                storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                    statusBarTileSettingList.writeDelimitedTo(it)
                    logger.d("write to: $storeFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}