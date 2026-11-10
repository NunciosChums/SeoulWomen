package kr.susemi99.seoulwomen.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DAY_NAME_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.KOREAN)

/**
 * 요일 이름. 화면의 다른 요일 표시(월화수...)와 맞추기 위해 기기 언어와 관계없이 한국어로 표시한다.
 */
val LocalDate.dayName: String
  get() = format(DAY_NAME_FORMATTER)

/**
 * 목록용 짧은 날짜. `11.2`처럼 월.일만 쓰고, [today]와 연도가 다를 때만 `2027.1.5`처럼 연도를 붙인다.
 */
fun LocalDate.shortText(today: LocalDate): String =
  if (year == today.year) "$monthValue.$dayOfMonth" else "$year.$monthValue.$dayOfMonth"

/**
 * 요일을 붙인 짧은 날짜. 예: `11.2(월)`
 */
fun LocalDate.shortTextWithDay(today: LocalDate): String = "${shortText(today)}($dayName)"
