package kr.susemi99.seoulwomen.util

import com.chibatching.kotpref.KotprefModel
import kr.susemi99.seoulwomen.R
import kr.susemi99.seoulwomen.application.App

/**
 * 앱 전체에서 사용할 정보 저장용
 */
object AppPreference : KotprefModel() {
  /**
   * 마지막 선택한 지역 이름(중랑, 강남...)
   * 기본: 장애
   */
  var lastSelectedAreaName by stringPref(App.instance.getString(R.string.default_area_name))

  /**
   * 마지막 선택한 지역 값(JungNang, GangNam...)
   * 기본: SeoulDisableWomenResourcesClass
   */
  var lastSelectedAreaValue by stringPref(App.instance.getString(R.string.default_area_value))
}