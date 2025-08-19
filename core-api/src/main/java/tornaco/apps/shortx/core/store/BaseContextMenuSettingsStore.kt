package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSettingList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseContextMenuSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("ContextMenuSettingsStore")

    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val settingsCache: MutableList<CustomContextMenuSetting> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllSettings(records: List<CustomContextMenuSetting>, write: Boolean = true) {
        synchronized(settingsCache) {
            records.forEach { record ->
                synchronized(settingsCache) {
                    settingsCache.removeIf { it.menu == record.menu }
                    settingsCache.add(record)
                }
            }
            if (write) postWrite()
        }
    }

    fun addSetting(setting: CustomContextMenuSetting) {
        synchronized(settingsCache) {
            settingsCache.removeIf { setting.menu == it.menu }
            settingsCache.add(setting)
            postWrite()
        }
    }

    fun deleteSetting(menu: CustomContextMenu) {
        synchronized(settingsCache) {
            settingsCache.removeIf { it.menu == menu }
            postWrite()
        }
    }

    fun getSettingByMenu(menu: CustomContextMenu): CustomContextMenuSetting? {
        synchronized(settingsCache) {
            return settingsCache.firstOrNull { it.menu == menu }
        }
    }

    fun getAllSettings(): List<CustomContextMenuSetting> {
        synchronized(settingsCache) {
            return settingsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        kotlin.runCatching {
            val settingList = atomicStoreFile.read {
                CustomContextMenuSettingList.parseDelimitedFrom(it)
            }
            synchronized(settingsCache) {
                settingsCache.clear()
                settingsCache.addAll(settingList.settingsList)
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

        synchronized(settingsCache) {
            val settingList = CustomContextMenuSettingList.newBuilder().apply {
                addAllSettings(settingsCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    settingList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}