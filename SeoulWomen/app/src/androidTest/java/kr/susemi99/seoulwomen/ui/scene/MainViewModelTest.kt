package kr.susemi99.seoulwomen.ui.scene

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.model.ResultItem
import kr.susemi99.seoulwomen.model.RowItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassParentItem
import kr.susemi99.seoulwomen.util.preference.AppPreference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList

/** 실제 [AppPreference]로 센터 선택이 저장되고 다음 요청에 반영되는지 확인한다. */
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
  private class FakeApi : Api {
    val requestedAreas = CopyOnWriteArrayList<String>()

    override suspend fun list(areaValue: String, startIndex: Int, endIndex: Int): WomenResourcesClassParentItem {
      requestedAreas += areaValue
      return WomenResourcesClassParentItem(
        classItem = WomenResourcesClassItem(0, ResultItem(ApiException.SUCCESS_CODE, "메시지"), emptyList())
      )
    }
  }

  @get:Rule
  val composeRule = createComposeRule()

  private val preference = AppPreference(InstrumentationRegistry.getInstrumentation().targetContext)
  private val api = FakeApi()

  @Before
  fun setUp() = preference.clear()

  @After
  fun tearDown() = preference.clear()

  private fun waitForRequest(area: Area) {
    composeRule.waitUntil(timeoutMillis = 5_000) { area.className in api.requestedAreas }
  }

  @Test
  fun 저장된_센터가_없으면_첫_센터를_표시한다() {
    assertEquals(Area.entries.first().title, MainViewModel(api, preference).title)
  }

  @Test
  fun 저장된_제목이_비어_있으면_첫_센터를_다시_저장한다() {
    preference.areaTitle = ""

    val viewModel = MainViewModel(api, preference)

    assertEquals(Area.entries.first().title, viewModel.title)
    assertEquals(Area.entries.first().title, preference.areaTitle)
    assertEquals(Area.entries.first().className, preference.areaClassName)
  }

  @Test
  fun 센터를_선택하면_제목과_저장값이_바뀌고_다시_열어도_유지된다() {
    MainViewModel(api, preference).apply {
      selectedArea(Area.Songpa)
      assertEquals(Area.Songpa.title, title)
    }

    assertEquals(Area.Songpa.title, preference.areaTitle)
    assertEquals(Area.Songpa.className, preference.areaClassName)
    assertEquals(Area.Songpa.title, MainViewModel(api, preference).title)
  }

  @Test
  fun 센터를_바꾸고_새로고침하면_새_센터의_목록을_요청한다() {
    val viewModel = MainViewModel(api, preference)
    lateinit var items: LazyPagingItems<RowItem>
    composeRule.setContent { items = viewModel.list.collectAsLazyPagingItems() }
    waitForRequest(Area.entries.first())

    composeRule.runOnIdle {
      viewModel.selectedArea(Area.Songpa)
      items.refresh()
    }

    waitForRequest(Area.Songpa)
    assertEquals(Area.Songpa.className, api.requestedAreas.last())
  }
}
