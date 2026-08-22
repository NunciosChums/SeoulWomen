package kr.susemi99.seoulwomen.util.preference

import android.content.Context
import kr.susemi99.seoulwomen.enums.Area

class AppPreference(context: Context) : BasePreference(context) {
  /**
   * 마지막 선택한 지역 이름(중랑, 강남...)
   * 기본: 장애 여성인력개발센터
   */
  var areaTitle by stringPref(Area.entries.first().title)

  /**
   * 마지막 선택한 지역 값(JungNang, GangNam...)
   * 기본: SeoulDisableWomenResourcesClass
   */
  var areaClassName by stringPref(Area.entries.first().className)
}