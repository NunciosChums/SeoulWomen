package kr.susemi99.seoulwomen.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 날짜 포맷 바꾸기
 */
object DateFormatter {
  const val ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ"
  const val YEAR_MONTH_DAY1 = "yyyyMMdd"
  const val YEAR_MONTH_DAY2 = "yyyy.MM.dd(E)"

  // 서버에서 보내주는 모든 패턴들. 가장 긴 걸 위로 올려야 한다.
  private val allPatterns = arrayOf(
    ISO8601,
    YEAR_MONTH_DAY1,
    YEAR_MONTH_DAY2
  )

  /**
   * 날짜(long)을 날짜 문자열로 변환
   */
  fun format(timeInMillis: Long, pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(timeInMillis)
  }

  /**
   * 날짜(long)을 ISO8601 형식으로 변환
   */
  fun format(timeInMillis: Long): String {
    return format(timeInMillis, ISO8601)
  }

  /**
   * 현재 시간을 날짜 문자열로 변화
   */
  fun format(pattern: String): String {
    return format(System.currentTimeMillis(), pattern)
  }

  /**
   * 시간 문자열을 다른 포맷으로 변경
   *
   * @param timeStr     2018-01-10 12:34:56
   * @param fromPattern yyyy-MM-dd HH:mm:ss
   * @param toPattern   yyyy/MM/dd
   * @return 2018-01-10
   */
  fun format(timeStr: String?, fromPattern: String, toPattern: String): String {
    if (timeStr.isNullOrBlank()) {
      return ""
    }

    val timeInMillis = dateStringToMillis(timeStr, fromPattern)
    return format(timeInMillis, toPattern)
  }

  /**
   * Date String 을 millisecond 로 변환.
   * patterns 에 하나라도 일치하는 포맷이 있으면 된다.
   *
   * @param timeStr 2016-10-21
   * @param pattern yyyy-MM-dd
   * @return millisecond
   */
  fun dateStringToMillis(timeStr: String?, pattern: String): Long {
    if (timeStr.isNullOrBlank()) {
      return 0
    }
    arrayOf(pattern).plus(allPatterns).forEach {
      try {
        return SimpleDateFormat(it, Locale.getDefault()).parse(timeStr).time
      } catch (e: ParseException) {
      }
    }
    return 0
  }
}