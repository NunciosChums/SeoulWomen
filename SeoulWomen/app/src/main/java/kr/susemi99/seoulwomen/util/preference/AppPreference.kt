package kr.susemi99.seoulwomen.util.preference

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.susemi99.seoulwomen.enums.Area
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(@ApplicationContext context: Context) : KotprefModel(context) {
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
