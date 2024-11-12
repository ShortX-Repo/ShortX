package tornaco.apps.shortx.ui.main.da

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.da.DirectAction

class DARepo(private val shortXManager: ShortXManager) {
    companion object {
        const val PAGE_SIZE = 3
    }

    fun getDAStream(): Flow<PagingData<DirectAction>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DAPagingSrc(shortXManager) }
        ).flow
    }
}