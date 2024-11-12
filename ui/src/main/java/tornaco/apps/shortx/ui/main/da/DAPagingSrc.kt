package tornaco.apps.shortx.ui.main.da

import androidx.paging.PagingSource
import androidx.paging.PagingState
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.ui.main.da.DARepo.Companion.PAGE_SIZE

private const val STARTING_PAGE_INDEX = 1

// 在 Paging 3 库中，getRefreshKey 是 PagingSource 中的一个方法，
// 用于指定刷新时需要加载的页面或键值。这个方法的作用是在执行刷新操作（例如用户下拉刷新）时，为 Paging 库提供一个合适的键值，以重新加载分页数据。
//
// getRefreshKey 方法的作用
// 当数据源刷新时，Paging 库会调用 getRefreshKey，
// 以确定从哪个位置开始加载数据。通常情况下，刷新操作会发生在以下几种情况：
//
// 用户下拉刷新。
// 预取的数据过期，需要重新加载。
// App 处于后台较长时间，数据需要更新。
// 实现 getRefreshKey
// getRefreshKey 的实现主要是根据当前 PagingState（包含了当前页面和锚定位置）
// 来确定一个适当的刷新键。通常情况下，你会想返回一个基于当前锚定位置的键，
// 以确保用户在数据刷新后，仍然可以看到与之前位置相近的数据。
class DAPagingSrc(private val shortXManager: ShortXManager) :
    PagingSource<Int, DirectAction>() {

    override fun getRefreshKey(state: PagingState<Int, DirectAction>): Int? {
        // 获取最近访问的页面索引（即锚定位置）
        val anchorPosition = state.anchorPosition ?: return null
        // 获取锚定位置附近的分页数据项
        val anchorPage = state.closestPageToPosition(anchorPosition)
        // 返回当前页面的键（通常是 page key 或 item index）
        return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirectAction> {
        return try {
            val position = params.key ?: STARTING_PAGE_INDEX
            shortXManager.getAllDirectAction()
            val repos = shortXManager.getAllDirectActionPaged(position, params.loadSize)
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}