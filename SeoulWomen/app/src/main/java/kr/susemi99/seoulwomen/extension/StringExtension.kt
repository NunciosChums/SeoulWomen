package kr.susemi99.seoulwomen.extension

import java.text.NumberFormat

/**
 * comma 찍기
 */
fun String.toCommaString(): String {
  return NumberFormat.getIntegerInstance().format(this.toFloat())
}