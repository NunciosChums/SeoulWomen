package kr.susemi99.seoulwomen.util

import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {
  override fun createStackElementTag(element: StackTraceElement) = "(${element.fileName}:${element.lineNumber})"
}
