package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItemList
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.enc.AESCrypt

class ClipboardStore(
    private val scope: CoroutineScope,
    private val encKey: String? = null
) {
    private val logger = Logger("ClipboardStore")
    private val storeFile by lazy { Dirs.dataStoreFile("Clip") }

    // un-encrypt
    private val cache: MutableList<ClipboardItem> = mutableListOf()

    private var job: Job? = null

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addText(text: ClipboardItem) {
        synchronized(cache) {
            cache.add(text)
            postWrite()
        }
    }

    fun deleteText(id: String) {
        synchronized(cache) {
            cache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun getText(id: String): ClipboardItem? {
        return synchronized(cache) {
            cache.firstOrNull { it.id == id }
        }
    }

    fun getAllText(): List<ClipboardItem> {
        synchronized(cache) {
            return cache.toList()
        }
    }

    fun clearAllText() {
        synchronized(cache) {
            cache.clear()
            postWrite()
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!storeFile.exists()) {
            logger.d("skip read no-exists file: $storeFile")
            return@withContext
        }
        kotlin.runCatching {
            synchronized(cache) {
                val list = storeFile.inputStream().use {
                    ClipboardItemList.parseDelimitedFrom(it)
                }
                cache.clear()
                cache.addAll(list.itemList.map {
                    ClipboardItem
                        .newBuilder(it)
                        .setValue(AESCrypt.decrypt(encKey, it.value))
                        .build()
                })
            }
        }.onFailure {
            logger.e(it, "read error")
        }
    }

    private fun postWrite() {
        job?.cancel()
        job = scope.launch {
            write()
        }
    }

    suspend fun write() = withContext(Dispatchers.IO) {
        logger.d("write.")
        synchronized(cache) {
            val list = ClipboardItemList.newBuilder().addAllItem(cache.map {
                ClipboardItem
                    .newBuilder(it)
                    .setValue(AESCrypt.encrypt(encKey, it.value))
                    .build()
            }).build()
            kotlin.runCatching {
                storeFile.apply { parentFile.mkdirs() }.outputStream().use {
                    list.writeDelimitedTo(it)
                    logger.d("write to: $storeFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}