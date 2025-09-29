package tornaco.apps.shortx.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryItemList
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CodeLibrarySortBy
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.util.AtomicFileCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.read
import tornaco.apps.shortx.core.util.writeInlined
import java.io.File

open class BaseCodeLibraryStore(
    private val scope: CoroutineScope,
    storeFile: File
) {
    private val logger = Logger("CodeLibraryStore")
    private val atomicStoreFile = AtomicFileCompat(storeFile)
    private val codeLibraryCache: MutableList<CodeLibraryItem> = mutableListOf()

    private var job: Job? = null

    companion object {
        private const val WRITE_DELAY = 1000L
    }

    suspend fun read() = readInternal()

    fun readBlocking() = runBlocking {
        readInternal()
    }

    fun addAllCodeLibraryItems(items: List<CodeLibraryItem>, write: Boolean = true) {
        synchronized(codeLibraryCache) {
            items.forEach { item ->
                codeLibraryCache.removeIf { it.id == item.id }
                codeLibraryCache.add(item)
            }
            if (write) postWrite()
        }
    }

    fun addCodeLibraryItem(item: CodeLibraryItem) {
        synchronized(codeLibraryCache) {
            codeLibraryCache.removeIf { it.id == item.id }
            codeLibraryCache.add(item)
            postWrite()
        }
    }

    fun updateCodeLibraryItem(item: CodeLibraryItem) {
        synchronized(codeLibraryCache) {
            val index = codeLibraryCache.indexOfFirst { it.id == item.id }
            if (index >= 0) {
                codeLibraryCache[index] = item
                postWrite()
            }
        }
    }

    fun deleteCodeLibraryItem(id: String) {
        synchronized(codeLibraryCache) {
            codeLibraryCache.removeIf { it.id == id }
            postWrite()
        }
    }

    fun deleteCodeLibraryItems(ids: List<String>) {
        synchronized(codeLibraryCache) {
            codeLibraryCache.removeIf { it.id in ids }
            postWrite()
        }
    }

    fun getCodeLibraryItem(id: String): CodeLibraryItem? {
        synchronized(codeLibraryCache) {
            return codeLibraryCache.firstOrNull { it.id == id }
        }
    }

    fun getAllCodeLibraryItems(): List<CodeLibraryItem> {
        synchronized(codeLibraryCache) {
            return codeLibraryCache.toList()
        }
    }

    fun queryCodeLibraryItems(query: CodeLibraryQuery): List<CodeLibraryItem> {
        synchronized(codeLibraryCache) {
            var result = codeLibraryCache.toList()

            // Filter by keyword
            if (query.keyword.isNotEmpty()) {
                result = result.filter { item ->
                    item.name.contains(query.keyword, ignoreCase = true) ||
                    item.description.contains(query.keyword, ignoreCase = true) ||
                    item.content.contains(query.keyword, ignoreCase = true) ||
                    item.tagsList.any { tag -> tag.contains(query.keyword, ignoreCase = true) }
                }
            }

            // Filter by type
            if (query.type != CodeType.UNRECOGNIZED) {
                result = result.filter { it.type == query.type }
            }

            // Filter by tags
            if (query.tagsList.isNotEmpty()) {
                result = result.filter { item ->
                    query.tagsList.any { queryTag ->
                        item.tagsList.contains(queryTag)
                    }
                }
            }

            // Sort results
            result = when (query.sortBy) {
                CodeLibrarySortBy.CodeLibrarySortBy_NAME_ASC -> result.sortedBy { it.name }
                CodeLibrarySortBy.CodeLibrarySortBy_NAME_DESC -> result.sortedByDescending { it.name }
                CodeLibrarySortBy.CodeLibrarySortBy_CREATED_ASC -> result.sortedBy { it.createdAt }
                CodeLibrarySortBy.CodeLibrarySortBy_CREATED_DESC -> result.sortedByDescending { it.createdAt }
                CodeLibrarySortBy.CodeLibrarySortBy_UPDATED_ASC -> result.sortedBy { it.updatedAt }
                CodeLibrarySortBy.CodeLibrarySortBy_UPDATED_DESC -> result.sortedByDescending { it.updatedAt }
                CodeLibrarySortBy.CodeLibrarySortBy_TYPE_ASC -> result.sortedBy { it.type.name }
                CodeLibrarySortBy.CodeLibrarySortBy_TYPE_DESC -> result.sortedByDescending { it.type.name }
                else -> result
            }

            return result
        }
    }

    fun getCodeLibraryItemCount(): Int {
        synchronized(codeLibraryCache) {
            return codeLibraryCache.size
        }
    }

    private suspend fun readInternal() = withContext(Dispatchers.IO) {
        if (!atomicStoreFile.exists()) {
            logger.d("skip read no-exists file: $atomicStoreFile")
            return@withContext
        }
        synchronized(codeLibraryCache) {
            kotlin.runCatching {
                val codeLibraryItemList = atomicStoreFile.read {
                    CodeLibraryItemList.parseDelimitedFrom(it)
                }
                codeLibraryItemList.run {
                    codeLibraryCache.clear()
                    codeLibraryCache.addAll(this.codeLibraryItemsList)
                }
            }.onFailure {
                logger.e(it, "read error")
            }
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
        synchronized(codeLibraryCache) {
            val codeLibraryItemList = CodeLibraryItemList.newBuilder().apply {
                addAllCodeLibraryItems(codeLibraryCache)
            }.build()
            kotlin.runCatching {
                atomicStoreFile.writeInlined {
                    codeLibraryItemList.writeDelimitedTo(it)
                    logger.d("write to: $atomicStoreFile")
                }
            }.onFailure {
                logger.e(it, "write error")
            }
        }
    }
}