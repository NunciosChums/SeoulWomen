package kr.susemi99.seoulwomen.model

/**
 * 접수 상태
 */
sealed interface ReceiveStatus {
  /** 접수 시작 전 */
  data object Upcoming : ReceiveStatus

  /** 접수 중. [daysLeft]는 마감일까지 남은 날 수이며 오늘 마감이면 0 */
  data class Open(val daysLeft: Int) : ReceiveStatus

  /** 접수 기간이 지남 */
  data object Closed : ReceiveStatus
}
