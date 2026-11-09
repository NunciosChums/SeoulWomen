package kr.susemi99.seoulwomen.ui.scene

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import kr.susemi99.seoulwomen.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 실제 Application과 Hilt 그래프로 앱이 뜨는지 확인한다.
 * 실제 API를 호출하지만 응답 결과와 무관하게 표시되는 상단바만 검사하므로 네트워크 상태에 영향받지 않는다.
 */
@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun 앱이_실행되면_상단바를_표시한다() {
    composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.content_description_open_menu)).assertIsDisplayed()
    composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.content_description_refresh)).assertIsDisplayed()
  }
}
