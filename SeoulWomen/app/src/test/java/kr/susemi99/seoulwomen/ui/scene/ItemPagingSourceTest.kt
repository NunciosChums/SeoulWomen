package kr.susemi99.seoulwomen.ui.scene

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.apiRespondingWith
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.fixture
import kr.susemi99.seoulwomen.model.ResultItem
import kr.susemi99.seoulwomen.model.RowItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassParentItem
import kr.susemi99.seoulwomen.rowItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ItemPagingSourceTest {
  private class FakeApi(private val respond: () -> WomenResourcesClassParentItem) : Api {
    val requestedAreas = mutableListOf<String>()
    val requestedRanges = mutableListOf<IntRange>()

    override suspend fun list(areaValue: String, startIndex: Int, endIndex: Int): WomenResourcesClassParentItem {
      requestedAreas += areaValue
      requestedRanges += startIndex..endIndex
      return respond()
    }
  }

  private val area = Area.Songpa.className
  private val fullPage = List(PAGE_SIZE) { rowItem(classCode = "C$it") }

  private fun success(totalCount: Int, code: String = ApiException.SUCCESS_CODE, rows: List<RowItem> = fullPage) =
    WomenResourcesClassParentItem(
      classItem = WomenResourcesClassItem(totalCount, ResultItem(code, "메시지"), rows)
    )

  private fun sourceFor(api: Api) = ItemPagingSource(api, area, PAGE_SIZE)

  private fun pagerFor(api: Api) = TestPager(PagingConfig(pageSize = PAGE_SIZE), sourceFor(api))

  private fun LoadResult<Int, RowItem>.asPage() = this as LoadResult.Page<Int, RowItem>

  private fun LoadResult<Int, RowItem>.asError() = this as LoadResult.Error<Int, RowItem>

  @Test
  fun `첫 페이지는 선택한 센터의 1번부터 30번까지 요청한다`() = runBlocking {
    val api = FakeApi { success(totalCount = 90) }

    val page = pagerFor(api).refresh().asPage()

    assertEquals(listOf(area), api.requestedAreas)
    assertEquals(listOf(1..30), api.requestedRanges)
    assertEquals(fullPage, page.data)
    assertNull(page.prevKey)
    assertEquals(1, page.nextKey)
  }

  @Test
  fun `다음 페이지는 31번부터 60번까지 요청한다`() = runBlocking {
    val api = FakeApi { success(totalCount = 90) }
    val pager = pagerFor(api)

    pager.refresh()
    val page = checkNotNull(pager.append()).asPage()

    assertEquals(listOf(1..30, 31..60), api.requestedRanges)
    assertEquals(0, page.prevKey)
    assertEquals(2, page.nextKey)
  }

  @Test
  fun `전체 개수가 페이지 크기와 같으면 다음 페이지가 없다`() = runBlocking {
    val page = pagerFor(FakeApi { success(totalCount = PAGE_SIZE) }).refresh().asPage()

    assertNull(page.nextKey)
  }

  @Test
  fun `전체 개수가 페이지 크기보다 하나 많으면 다음 페이지가 있다`() = runBlocking {
    val page = pagerFor(FakeApi { success(totalCount = PAGE_SIZE + 1) }).refresh().asPage()

    assertEquals(1, page.nextKey)
  }

  @Test
  fun `성공 코드에 목록이 비어 있으면 빈 페이지가 된다`() = runBlocking {
    val page = pagerFor(FakeApi { success(totalCount = 0, rows = emptyList()) }).refresh().asPage()

    assertTrue(page.data.isEmpty())
    assertNull(page.prevKey)
    assertNull(page.nextKey)
  }

  @Test
  fun `실제 결과 없음 응답은 빈 페이지가 된다`() = runBlocking {
    val page = pagerFor(apiRespondingWith(fixture("list_no_data.json"))).refresh().asPage()

    assertTrue(page.data.isEmpty())
    assertNull(page.prevKey)
    assertNull(page.nextKey)
  }

  @Test
  fun `최상위 RESULT의 오류 코드는 ApiException이 된다`() = runBlocking {
    val api = FakeApi { WomenResourcesClassParentItem(result = ResultItem("ERROR-500", "서버 오류")) }

    val error = pagerFor(api).refresh().asError().throwable as ApiException

    assertEquals("ERROR-500", error.code)
    assertEquals("서버 오류", error.message)
  }

  @Test
  fun `classItem의 오류 코드도 ApiException이 된다`() = runBlocking {
    val api = FakeApi { success(totalCount = 0, code = "ERROR-600", rows = emptyList()) }

    val error = pagerFor(api).refresh().asError().throwable as ApiException

    assertEquals("ERROR-600", error.code)
  }

  @Test
  fun `네트워크 예외는 LoadResult Error로 전달한다`() = runBlocking {
    val exception = IOException("네트워크 오류")

    val result = pagerFor(FakeApi { throw exception }).refresh().asError()

    assertSame(exception, result.throwable)
  }

  @Test
  fun `코루틴 취소는 LoadResult Error로 바꾸지 않고 다시 던진다`() {
    val source = sourceFor(FakeApi { throw CancellationException("취소") })

    assertThrows(CancellationException::class.java) {
      runBlocking { source.load(LoadParams.Refresh(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false)) }
    }
  }

  @Test
  fun `실제 XML 오류 응답은 ApiException이 된다`() = runBlocking {
    val result = pagerFor(apiRespondingWith(fixture("list_invalid_key.xml"))).refresh().asError()

    assertEquals("INFO-100", (result.throwable as ApiException).code)
  }

  @Test
  fun `새로고침은 보고 있던 위치와 관계없이 첫 페이지부터 시작한다`() = runBlocking {
    val secondPage = List(PAGE_SIZE) { rowItem(classCode = "D$it") }
    val pages = ArrayDeque(listOf(fullPage, secondPage))
    val source = sourceFor(FakeApi { success(totalCount = 90, rows = pages.removeFirst()) })
    val pager = TestPager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false), source)
    pager.refresh()
    pager.append()

    assertNull(source.getRefreshKey(pager.getPagingState(anchorPosition = 10)))
    assertNull(source.getRefreshKey(pager.getPagingState(anchorPosition = 40)))
  }

  @Test
  fun `이전 페이지와 겹치는 항목은 다음 페이지에서 제외한다`() = runBlocking {
    val pages = ArrayDeque(listOf(fullPage, listOf(fullPage.last(), rowItem(classCode = "C30"))))
    val pager = pagerFor(FakeApi { success(totalCount = 90, rows = pages.removeFirst()) })

    pager.refresh()
    val page = checkNotNull(pager.append()).asPage()

    assertEquals(listOf(rowItem(classCode = "C30")), page.data)
    assertEquals(2, page.nextKey)
  }

  @Test
  fun `한 페이지 안에서 겹치는 항목도 제외한다`() = runBlocking {
    val rows = listOf(rowItem(classCode = "C1"), rowItem(classCode = "C1"), rowItem(classCode = "C2"))

    val page = pagerFor(FakeApi { success(totalCount = 3, rows = rows) }).refresh().asPage()

    assertEquals(listOf(rowItem(classCode = "C1"), rowItem(classCode = "C2")), page.data)
  }

  private companion object {
    const val PAGE_SIZE = 30
  }
}
