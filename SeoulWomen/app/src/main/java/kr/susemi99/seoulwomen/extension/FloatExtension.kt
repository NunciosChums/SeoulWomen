package kr.susemi99.seoulwomen.extension

import java.text.NumberFormat

val Float.comma: String
  get() = NumberFormat.getIntegerInstance().format(this)