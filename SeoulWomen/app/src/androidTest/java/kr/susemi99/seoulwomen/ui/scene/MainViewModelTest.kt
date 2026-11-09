package kr.susemi99.seoulwomen.ui.scene

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kr.susemi99.seoulwomen.api.Api
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.model.ResultItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassItem
import kr.susemi99.seoulwomen.model.WomenResourcesClassParentItem
import kr.susemi99.seoulwomen.util.preference.AppPreference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  private val preference = AppPreference(context)
  private val api = FakeApi()

  @Before
  fun setUp() = preference.clear()

  @After
  fun tearDown() = preference.clear()

  private fun savedPreferences() = context.getSharedPreferences(preference.kotprefName, Context.MODE_PRIVATE)

  private fun waitForRequest(area: Area) {
    composeRule.waitUntil(timeoutMillis = 5_000) { area.className in api.requestedAreas }
  }

  @Test
  fun 저장된_센터가_없으면_첫_센터를_표시한다() {
    assertEquals(Area.entries.first(), MainViewModel(api, preference).area.value)
  }

  @Test
  fun 센터를_선택하면_다시_열어도_유지된다() {
    MainViewModel(api, preference).apply {
      selectArea(Area.Songpa)
      assertEquals(Area.Songpa, area.value)
    }

    assertEquals(Area.Songpa, preference.area)
    assertEquals(Area.Songpa, MainViewModel(api, preference).area.value)
  }

  @Test
  fun 이전_형식으로_저장된_센터를_옮기고_이전_값을_지운다() {
    savedPreferences().edit()
      .putString("areaTitle", Area.Songpa.title)
      .putString("areaClassName", Area.Songpa.className)
      .commit()

    assertEquals(Area.Songpa, AppPreference(context).area)
    assertFalse(savedPreferences().contains("areaTitle"))
    assertFalse(savedPreferences().contains("areaClassName"))
  }

  @Test
  fun 이전_형식의_값을_알_수_없으면_첫_센터를_표시한다() {
    savedPreferences().edit().putString("areaClassName", "없는 센터").commit()

    assertEquals(Area.entries.first(), AppPreference(context).area)
  }

  @Test
  fun 센터를_선택하면_새로고침_없이_새_센터의_목록을_요청한다() {
    val viewModel = MainViewModel(api, preference)
    composeRule.setContent { viewModel.list.collectAsLazyPagingItems() }
    waitForRequest(Area.entries.first())

    composeRule.runOnIdle { viewModel.selectArea(Area.Songpa) }

    waitForRequest(Area.Songpa)
    assertEquals(Area.Songpa.className, api.requestedAreas.last())
  }
}
