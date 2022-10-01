package kr.susemi99.seoulwomen.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 요일 이름
 */
val LocalDate.dayName: String
  get() = format(DateTimeFormatter.ofPattern("E"))