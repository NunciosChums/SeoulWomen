package kr.susemi99.seoulwomen.util.preference

import android.content.Context
import kr.susemi99.seoulwomen.R

class AppPreference(context: Context) : BasePreference(context) {
  /**
   * 마지막 선택한 지역 이름(중랑, 강남...)
   * 기본: 장애 여성인력개발센터
   */
  var lastSelectedAreaNameV2 by stringPref(context.getString(R.string.default_area_name))

  /**
   * 마지막 선택한 지역 값(JungNang, GangNam...)
   * 기본: SeoulDisableWomenResourcesClass
   */
  var lastSelectedAreaValue by stringPref(context.getString(R.string.default_area_value))
}