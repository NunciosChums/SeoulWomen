package kr.susemi99.seoulwomen.ui.scene

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.model.RowItem

/**
 * 서울 열린데이터 API용 PagingSource.
 * 키는 0부터 시작하는 페이지 번호이며, API의 start_index/end_index는 1부터 시작하는 양끝 포함 값이다.
 */
class ItemPagingSource(
  private val api: Api,
  private val areaClassName: String,
  private val pageSize: Int,
) : PagingSource<Int, RowItem>() {

  override fun getRefreshKey(state: PagingState<Int, RowItem>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowItem> {
    val page = params.key ?: 0
    val startIndex = page * pageSize + 1
    val endIndex = startIndex + pageSize - 1

    return try {
      val result = api.list(startIndex = startIndex, endIndex = endIndex, areaValue = areaClassName)
      result.resultItem?.let {
        if (it.code != ApiException.SUCCESS_CODE) throw ApiException(it.code, it.message)
      }
      val totalCount = result.classItem?.listTotalCount ?: 0
      LoadResult.Page(
        data = result.classItem?.rows ?: listOf(),
        prevKey = if (page == 0) null else page - 1,
        nextKey = if (endIndex < totalCount) page + 1 else null,
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}
