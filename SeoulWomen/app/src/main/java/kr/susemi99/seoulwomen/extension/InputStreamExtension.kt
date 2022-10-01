package kr.susemi99.seoulwomen.extension

import java.io.InputStream

val InputStream.toString
  get() = String(readBytes())