package kr.susemi99.seoulwomen.ui.scene

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.flowOf
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.api.ApiException
import kr.susemi99.seoulwomen.enums.Area
import kr.susemi99.seoulwomen.model.RowItem
import kr.susemi99.seoulwomen.ui.theme.SeoulWomenTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

/** 네트워크 없이 목록 화면의 상태별 표시와 드로어 동작을 확인한다. */
@RunWith(AndroidJUnit4::class)
class MainContentTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  private val notLoading = LoadState.NotLoading(endOfPaginationReached = true)
  private val loaded = LoadStates(refresh = notLoading, prepend = notLoading, append = notLoading)
  private val currentArea = Area.Disable
  private val otherArea = Area.Dongjak
  private val selectedAreas = mutableListOf<Area>()

  private fun show(data: PagingData<RowItem>) {
    val flow = flowOf(data)
    composeRule.setContent {
      SeoulWomenTheme {
        MainContent(
          area = currentArea,
          listItems = flow.collectAsLazyPagingItems(),
          onAreaSelected = { selectedAreas += it },
        )
      }
    }
  }

  private fun emptyResult() = PagingData.empty<RowItem>(loaded)

  private fun showError(error: Throwable) {
    show(PagingData.empty(LoadStates(refresh = LoadState.Error(error), prepend = notLoading, append = notLoading)))
  }

  private fun clickDrawerItem(area: Area) {
    composeRule.onNodeWithContentDescription(context.getString(R.string.content_description_open_menu)).performClick()
    composeRule.onNode(hasText(area.title) and hasClickAction()).performClick()
    composeRule.waitForIdle()
  }

  @Test
  fun 목록_항목의_내용을_표시한다() {
    show(PagingData.from(listOf(row), sourceLoadStates = loaded))

    // 닫힌 드로어에도 같은 제목의 항목이 있으므로 클릭할 수 없는 상단바 제목만 찾는다.
    composeRule.onNode(hasText(currentArea.title) and !hasClickAction()).assertIsDisplayed()
    composeRule.onNodeWithText("[초급] 테스트 강좌").assertIsDisplayed()
    composeRule.onNodeWithText("${context.getString(R.string.label_receive_period)}: ").assertIsDisplayed()
    composeRule.onNodeWithText("5/20명").assertIsDisplayed()
    composeRule.onNodeWithText("100,000원").assertIsDisplayed()
    composeRule.onNodeWithText("방문, 온라인").assertIsDisplayed()
  }

  @Test
  fun 결과가_없으면_안내_문구를_표시한다() {
    show(emptyResult())

    composeRule.onNodeWithText(context.getString(R.string.no_result)).assertIsDisplayed()
  }

  @Test
  fun API_오류면_메시지와_코드_재시도_버튼을_표시한다() {
    showError(ApiException("ERROR-500", "서버 오류"))

    composeRule.onNodeWithText(context.getString(R.string.error_title)).assertIsDisplayed()
    composeRule.onNodeWithText("서버 오류 (ERROR-500)").assertIsDisplayed()
    composeRule.onNodeWithText(context.getString(R.string.retry)).assertIsDisplayed()
  }

  @Test
  fun 네트워크_예외면_원문_대신_안내_문구를_표시한다() {
    showError(IOException("Unable to resolve host"))

    composeRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
    composeRule.onNodeWithText("Unable to resolve host").assertDoesNotExist()
  }

  @Test
  fun 기타_예외면_원문_대신_알_수_없는_오류를_표시한다() {
    showError(IllegalStateException("내부 메시지"))

    composeRule.onNodeWithText(context.getString(R.string.error_unknown)).assertIsDisplayed()
    composeRule.onNodeWithText("내부 메시지").assertDoesNotExist()
  }

  @Test
  fun 재시도_버튼을_누르면_콜백을_호출한다() {
    var retryCount = 0
    composeRule.setContent {
      SeoulWomenTheme {
        ErrorView(throwable = IOException("오류"), onRetry = { retryCount++ })
      }
    }

    composeRule.onNodeWithText(context.getString(R.string.retry)).performClick()

    composeRule.runOnIdle { assertEquals(1, retryCount) }
  }

  @Test
  fun 다음_페이지_로드에_실패하면_목록_아래에_재시도_버튼을_표시한다() {
    val appendError = LoadStates(refresh = notLoading, prepend = notLoading, append = LoadState.Error(IOException("오류")))
    show(PagingData.from(listOf(row), sourceLoadStates = appendError))

    composeRule.onNodeWithText("[초급] 테스트 강좌").assertIsDisplayed()
    composeRule.onNodeWithText(context.getString(R.string.error_title)).assertIsDisplayed()
    composeRule.onNodeWithText(context.getString(R.string.retry)).assertIsDisplayed()
  }

  @Test
  fun 다음_페이지_재시도_버튼을_누르면_콜백을_호출한다() {
    var retryCount = 0
    composeRule.setContent {
      SeoulWomenTheme {
        AppendErrorView(onRetry = { retryCount++ })
      }
    }

    composeRule.onNodeWithText(context.getString(R.string.retry)).performClick()

    composeRule.runOnIdle { assertEquals(1, retryCount) }
  }

  @Test
  fun 드로어를_스크롤해_마지막_센터까지_볼_수_있다() {
    show(emptyResult())

    composeRule.onNodeWithContentDescription(context.getString(R.string.content_description_open_menu)).performClick()
    composeRule.onNode(hasText(Area.entries.last().title) and hasClickAction()).performScrollTo().assertIsDisplayed()
  }

  @Test
  fun 드로어에서_다른_센터를_고르면_콜백을_호출한다() {
    show(emptyResult())

    clickDrawerItem(otherArea)

    composeRule.runOnIdle { assertEquals(listOf(otherArea), selectedAreas) }
  }

  @Test
  fun 드로어에서_현재_센터를_고르면_콜백을_호출하지_않는다() {
    show(emptyResult())

    clickDrawerItem(currentArea)

    composeRule.runOnIdle { assertEquals(emptyList<Area>(), selectedAreas) }
  }

  private val row = RowItem(
    classCode = "C1",
    className = "테스트 강좌",
    organCode = "03",
    organName = "테스트 센터",
    difficultyCode = "J",
    difficultyName = "초급",
    receiveFrom = LocalDate.of(2026, 3, 2),
    receiveTo = LocalDate.of(2026, 3, 6),
    receiveTimeFrom = "09:00",
    receiveTimeTo = "18:00",
    educateFrom = LocalDate.of(2026, 3, 9),
    educateTo = LocalDate.of(2026, 3, 13),
    educateTimeFrom = "14:00",
    educateTimeTo = "17:00",
    monday = "Y",
    tuesday = "N",
    wednesday = "Y",
    thursday = "N",
    friday = "Y",
    saturday = "N",
    sunday = "N",
    collectNum = 20,
    spareNum = 5,
    educateFee = 100000,
    visitReceiveFlag = "Y",
    onlineReceiveFlag = "Y",
    url = "https://example.com/class",
  )
}
