package kr.susemi99.seoulwomen.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.susemi99.seoulwomen.extension.shortText
import kr.susemi99.seoulwomen.extension.shortTextWithDay
import kr.susemi99.seoulwomen.util.serializer.DecimalIntSerializer
import kr.susemi99.seoulwomen.util.serializer.TimeTextSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Serializable
data class RowItem(
  @SerialName("CLASS_CODE") val classCode: String,
  @SerialName("CLASS_NAME") val className: String,
  @SerialName("ORGAN_CODE") val organCode: String,
  @SerialName("ORGAN_NAME") val organName: String,
  @SerialName("DIFFICULTY") val difficultyCode: String,
  @SerialName("DIFFICULTY_NAME") val difficultyName: String,
  @Contextual @SerialName("RECEIVE_FROM") val receiveFrom: LocalDate,
  @Contextual @SerialName("RECEIVE_TO") val receiveTo: LocalDate,
  @Serializable(with = TimeTextSerializer::class) @SerialName("RECEIVE_TIME_FROM") val receiveTimeFrom: String,
  @Serializable(with = TimeTextSerializer::class) @SerialName("RECEIVE_TIME_TO") val receiveTimeTo: String,
  @Contextual @SerialName("EDUCATE_FROM") val educateFrom: LocalDate,
  @Contextual @SerialName("EDUCATE_TO") val educateTo: LocalDate,
  @Serializable(with = TimeTextSerializer::class) @SerialName("EDUCATE_TIME_FROM") val educateTimeFrom: String,
  @Serializable(with = TimeTextSerializer::class) @SerialName("EDUCATE_TIME_TO") val educateTimeTo: String,
  @SerialName("MONDAY") val monday: String?,
  @SerialName("TUESDAY") val tuesday: String?,
  @SerialName("WEDNESDAY") val wednesday: String?,
  @SerialName("THURSDAY") val thursday: String?,
  @SerialName("FRIDAY") val friday: String?,
  @SerialName("SATURDAY") val saturday: String?,
  @SerialName("SUNDAY") val sunday: String?,
  @Serializable(with = DecimalIntSerializer::class) @SerialName("COLLECT_NUM") val collectNum: Int,
  @Serializable(with = DecimalIntSerializer::class) @SerialName("SPARE_NUM") val spareNum: Int,
  @Serializable(with = DecimalIntSerializer::class) @SerialName("EDUCATE_FEE") val educateFee: Int,
  @SerialName("VISIT_RECEIVE_FLAG") val visitReceiveFlag: String,
  @SerialName("ONLINE_RECEIVE_FLAG") val onlineReceiveFlag: String,
  @SerialName("URL") val url: String
) {
  /**
   * 목록 키: 센터(organCode)와 강좌 코드 조합
   */
  val key
    get() = "$organCode-$classCode"

  /**
   * 접수 상태. 마감 시각이 지났으면 마감일 당일이라도 [ReceiveStatus.Closed]다.
   */
  fun receiveStatus(now: LocalDateTime): ReceiveStatus = when {
    now < receiveFrom.atTimeText(receiveTimeFrom) -> ReceiveStatus.Upcoming
    now >= receiveTo.atTimeText(receiveTimeTo) -> ReceiveStatus.Closed
    else -> ReceiveStatus.Open(daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), receiveTo).toInt())
  }

  /**
   * 신청 기간. 예: `11.2(월) 09:00 ~ 11.13(금) 18:00`
   */
  fun receivePeriod(today: LocalDate) = if (receiveFrom == receiveTo) {
    "${receiveFrom.shortTextWithDay(today)} $receiveTimeFrom~$receiveTimeTo"
  } else {
    "${receiveFrom.shortTextWithDay(today)} $receiveTimeFrom ~ ${receiveTo.shortTextWithDay(today)} $receiveTimeTo"
  }

  /**
   * 교육 일정. 하루짜리는 `9.5(토) 10:00~12:00`, 기간 교육은 `11.16 ~ 12.18 · 월·수·금 10:00~12:00`
   */
  fun educateSchedule(today: LocalDate) = if (educateFrom == educateTo) {
    "${educateFrom.shortTextWithDay(today)} $educateTime"
  } else {
    val weekdaysAndTime = listOf(educateWeekdays, educateTime).filter { it.isNotEmpty() }.joinToString(" ")
    "${educateFrom.shortText(today)} ~ ${educateTo.shortText(today)} · $weekdaysAndTime"
  }

  /**
   * 교육 요일
   */
  private val educateWeekdays
    get() = buildList {
      if (monday == "Y") add("월")
      if (tuesday == "Y") add("화")
      if (wednesday == "Y") add("수")
      if (thursday == "Y") add("목")
      if (friday == "Y") add("금")
      if (saturday == "Y") add("토")
      if (sunday == "Y") add("일")
    }.joinToString("·")

  /**
   * 교육 시간
   */
  private val educateTime
    get() = "$educateTimeFrom~$educateTimeTo"

  /**
   * 남은 자리가 없음
   */
  val isFull
    get() = spareNum <= 0

  /**
   * 남은 자리가 정원의 20% 이하
   */
  val isAlmostFull
    get() = !isFull && spareNum * 5 <= collectNum

  /**
   * 정원 대비 신청 인원 비율(0~1)
   */
  val enrolledRatio: Float
    get() = when {
      isFull -> 1f
      collectNum <= 0 -> 0f
      else -> ((collectNum - spareNum).toFloat() / collectNum).coerceIn(0f, 1f)
    }

  /**
   * 무료 강좌
   */
  val isFree
    get() = educateFee <= 0

  /**
   * 수강료
   */
  val fee
    get() = if (isFree) "무료" else "%,d원".format(educateFee)

  /**
   * 접수 방법
   */
  val registerMethods
    get() = buildList {
      if (visitReceiveFlag == "Y") add("방문")
      if (onlineReceiveFlag == "Y") add("온라인")
    }.ifEmpty { listOf("접수방법 확인") }
}

/**
 * "HH:mm" 문자열을 이 날짜의 시각으로 바꾼다. 자정 마감을 뜻하는 "24:00"은 다음 날 0시로 본다.
 */
private fun LocalDate.atTimeText(time: String): LocalDateTime =
  if (time == "24:00") plusDays(1).atStartOfDay() else atTime(LocalTime.parse(time))
