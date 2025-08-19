package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSettingList
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseDynamicShortcutSettingsStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("DynamicShortcutSettingsStore")

    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val settingsCache: MutableList<DynamicShortcutSetting> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllSettings(records: List<DynamicShortcutSetting>, write: Boolean = true) {
        synchronized(settingsCache) {
            records.forEach { record ->
                synchronized(settingsCache) {
                    settingsCache.removeIf { it.shortcut == record.shortcut }
                    settingsCache.add(record)
                }
            }
            if (write) postWrite()
        }
    }

    fun addSetting(setting: DynamicShortcutSetting) {
        synchronized(settingsCache) {
            settingsCache.removeIf { setting.shortcut == it.shortcut }
            settingsCache.add(setting)
            postWrite()
        }
    }

    fun deleteSetting(shortcut: DynamicShortcut) {
        synchronized(settingsCache) {
            settingsCache.removeIf { it.shortcut == shortcut }
            postWrite()
        }
    }

    fun getSettingByShortcut(shortcut: DynamicShortcut): DynamicShortcutSetting? {
        synchronized(settingsCache) {
            return settingsCache.firstOrNull { it.shortcut == shortcut }
        }
    }

    fun getAllSettings(): List<DynamicShortcutSetting> {
        synchronized(settingsCache) {
            return settingsCache.toList()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        runCatching {
            val settingList = atomicStoreFile.read {
                DynamicShortcutSettingList.parseDelimitedFrom(it)
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
            val statusBarTileSettingList = DynamicShortcutSettingList.newBuilder().apply {
                addAllSettings(settingsCache)
            }.build()
            runCatching {
                atomicStoreFile.writeInlined {
                    statusBarTileSettingList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}