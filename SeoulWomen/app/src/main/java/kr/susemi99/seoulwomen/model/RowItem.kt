package kr.susemi99.seoulwomen.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.susemi99.seoulwomen.extension.dayName
import kr.susemi99.seoulwomen.util.serializer.DecimalIntSerializer
import kr.susemi99.seoulwomen.util.serializer.TimeTextSerializer
import java.time.LocalDate

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
   * 난이도
   */
  val difficulty
    get() = "[${difficultyName}]"

  /**
   * 신청 기간
   */
  val receivePeriod
    get() = if (receiveFrom == receiveTo) {
      "$receiveFrom(${receiveFrom.dayName}) $receiveTimeFrom ~ $receiveTimeTo"
    } else {
      "$receiveFrom(${receiveFrom.dayName}) $receiveTimeFrom ~\n$receiveTo(${receiveTo.dayName}) $receiveTimeTo"
    }

  /**
   * 교육 일정: 하루짜리는 날짜와 시간, 기간 교육은 기간·요일·시간을 줄바꿈으로 구분
   */
  val educateSchedule
    get() = if (educateFrom == educateTo) {
      "$educateFrom(${educateFrom.dayName}) $educateTime"
    } else {
      "$educateFrom(${educateFrom.dayName}) ~ $educateTo(${educateTo.dayName})\n$educateWeekdays\n$educateTime"
    }

  /**
   * 교육 요일
   */
  private val educateWeekdays
    get() = buildString {
      if (monday == "Y") append("월")
      if (tuesday == "Y") append("화")
      if (wednesday == "Y") append("수")
      if (thursday == "Y") append("목")
      if (friday == "Y") append("금")
      if (saturday == "Y") append("토")
      if (sunday == "Y") append("일")
    }

  /**
   * 교육 시간
   */
  private val educateTime
    get() = "$educateTimeFrom ~ $educateTimeTo"

  /**
   * 잔여
   */
  val remainNumber
    get() = "$spareNum/${collectNum}명"

  /**
   * 수강료
   */
  val fee
    get() = if (educateFee > 0) {
      "%,d원".format(educateFee)
    } else {
      "무료"
    }

  /**
   * 접수 방법
   */
  val howToRegister
    get() = buildList {
      if (visitReceiveFlag == "Y") add("방문")
      if (onlineReceiveFlag == "Y") add("온라인")
    }.joinToString().ifEmpty { "접수방법 확인" }
}
