package kr.susemi99.seoulwomen.ui.scene

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.model.RowItem

class ItemPagingSource constructor(
  private val api: Api,
  private val areaClassName: String,
) : PagingSource<Int, RowItem>() {
  private val itemsPerPage = 30
  private var prevAreaClassName: String? = null

  override fun getRefreshKey(state: PagingState<Int, RowItem>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(itemsPerPage) ?: anchorPage?.nextKey?.minus(itemsPerPage)
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowItem> {
    var nextPage = params.key ?: itemsPerPage
    var currentPage = nextPage - itemsPerPage
    if (prevAreaClassName != areaClassName) {
      prevAreaClassName = areaClassName
      nextPage = itemsPerPage
      currentPage = 0
    }
    val result = api.list(startIndex = currentPage, endIndex = nextPage, areaValue = areaClassName)

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