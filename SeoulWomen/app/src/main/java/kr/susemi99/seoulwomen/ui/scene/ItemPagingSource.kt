package kr.susemi99.seoulwomen.ui.scene

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.model.RowItem
import kr.susemi99.seoulwomen.util.preference.AppPreference

class ItemPagingSource constructor(
  private val api: Api,
  private val appPreference: AppPreference
) : PagingSource<Int, RowItem>() {
  private val itemsPerPage = 30

  override fun getRefreshKey(state: PagingState<Int, RowItem>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(itemsPerPage) ?: anchorPage?.nextKey?.minus(itemsPerPage)
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowItem> {
    val nextPage = params.key ?: itemsPerPage
    val currentPage = nextPage - itemsPerPage
//    val result = api.list(startIndex = currentPage, endIndex = nextPage, areaValue = "SeoulJungNangWomenResourcesClass")
    val result = api.list(startIndex = currentPage, endIndex = nextPage, areaValue = appPreference.lastSelectedAreaValue)

    return try {
      LoadResult.Page(
        data = result.classItem?.rows ?: listOf(),
        prevKey = if (nextPage == itemsPerPage) null else nextPage - 1,
        nextKey = if (nextPage < (result.classItem?.listTotalCount ?: 0)) nextPage.plus(itemsPerPage) else null
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}