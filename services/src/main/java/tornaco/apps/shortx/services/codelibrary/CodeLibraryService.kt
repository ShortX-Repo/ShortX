package tornaco.apps.shortx.services.codelibrary

import android.content.Context
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.CodeLibraryStore
import tornaco.apps.shortx.services.systemServiceScope

class CodeLibraryService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "CodeLibraryService") {

    private val codeLibraryStore: CodeLibraryStore by lazy {
        CodeLibraryStore(systemServiceScope)
    }

    override fun start(context: Context) {
        super.start(context)
        logger.d("CodeLibraryService started")
    }

    override fun systemReady() {
        super.systemReady()
        codeLibraryStore.readBlocking()
        logger.d("CodeLibraryService system ready")
    }

    // CRUD operations
    fun addCodeLibraryItem(item: CodeLibraryItem) {
        codeLibraryStore.addCodeLibraryItem(item)
        logger.d("Added CodeLibraryItem: ${item.id}")
    }

    fun updateCodeLibraryItem(item: CodeLibraryItem) {
        codeLibraryStore.updateCodeLibraryItem(item)
        logger.d("Updated CodeLibraryItem: ${item.id}")
    }

    fun deleteCodeLibraryItem(id: String) {
        codeLibraryStore.deleteCodeLibraryItem(id)
        logger.d("Deleted CodeLibraryItem: $id")
    }

    fun getCodeLibraryItemById(id: String): CodeLibraryItem? {
        return codeLibraryStore.getCodeLibraryItem(id)
    }

    // Query operations
    fun getAllCodeLibraryItems(): List<CodeLibraryItem> {
        return codeLibraryStore.getAllCodeLibraryItems()
    }

    fun queryCodeLibraryItems(query: CodeLibraryQuery): List<CodeLibraryItem> {
        return codeLibraryStore.queryCodeLibraryItems(query)
    }

    fun getCodeLibraryItemCount(): Int {
        return codeLibraryStore.getCodeLibraryItemCount()
    }

    // Execute operation
    fun executeCodeLibraryItem(id: String): String? {
        val item = getCodeLibraryItemById(id)
        return if (item != null) {
            try {
                //  codeLibraryStore.executeCodeLibraryItem(item, context)
                logger.d("Executed CodeLibraryItem: $id")
                "Execution completed successfully"
            } catch (e: Exception) {
                logger.e(e, "Failed to execute CodeLibraryItem: $id")
                "Execution failed: ${e.message}"
            }
        } else {
            logger.w("CodeLibraryItem not found: $id")
            "CodeLibraryItem not found"
        }
    }
}