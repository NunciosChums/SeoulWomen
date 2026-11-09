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
