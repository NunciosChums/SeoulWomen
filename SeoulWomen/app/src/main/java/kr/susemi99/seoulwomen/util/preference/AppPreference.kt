package kr.susemi99.seoulwomen.util.preference

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.susemi99.seoulwomen.enums.Area
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(@ApplicationContext context: Context) : KotprefModel(context) {
  private var areaName by nullableStringPref()

  // 버전 17까지 쓰던 키. 기존 저장값을 areaName으로 옮긴 뒤 지운다.
  private var areaTitle by nullableStringPref()
  private var areaClassName by nullableStringPref()

  init {
    migrateLegacyArea()
  }

  /**
   * 마지막 선택한 지역
   * 기본: 장애 여성인력개발센터
   */
  var area: Area
    get() = Area.entries.find { it.name == areaName } ?: Area.entries.first()
    set(value) {
      areaName = value.name
    }

  private fun migrateLegacyArea() {
    val legacyClassName = areaClassName ?: return
    if (areaName == null) {
      areaName = Area.entries.find { it.className == legacyClassName }?.name
    }
    areaTitle = null
    areaClassName = null
  }
}
