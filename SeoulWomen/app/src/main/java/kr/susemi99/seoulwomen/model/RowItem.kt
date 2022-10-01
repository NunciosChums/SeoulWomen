package kr.susemi99.seoulwomen.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.susemi99.seoulwomen.extension.comma
import kr.susemi99.seoulwomen.extension.dayName
import java.time.LocalDate
import java.time.LocalTime

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
  @Contextual @SerialName("RECEIVE_TIME_FROM") val receiveTimeFrom: LocalTime,
  @Contextual @SerialName("RECEIVE_TIME_TO") val receiveTimeTo: LocalTime,
  @Contextual @SerialName("EDUCATE_FROM") val educateFrom: LocalDate,
  @Contextual @SerialName("EDUCATE_TO") val educateTo: LocalDate,
  @Contextual @SerialName("EDUCATE_TIME_FROM") val educateTimeFrom: LocalTime,
  @Contextual @SerialName("EDUCATE_TIME_TO") val educateTimeTo: LocalTime,
  @SerialName("MONDAY") val monday: String?,
  @SerialName("TUESDAY") val tuesday: String?,
  @SerialName("WEDNESDAY") val wednesday: String?,
  @SerialName("THURSDAY") val thursday: String?,
  @SerialName("FRIDAY") val friday: String?,
  @SerialName("SATURDAY") val saturday: String?,
  @SerialName("SUNDAY") val sunday: String?,
  @SerialName("COLLECT_NUM") val collectNum: Float,
  @SerialName("SPARE_NUM") val spareNum: Float,
  @SerialName("EDUCATE_FEE") val educateFee: Float,
  @SerialName("VISIT_RECEIVE_FLAG") val visitReceiveFlag: String,
  @SerialName("ONLINE_RECEIVE_FLAG") val onlineReceiveFlag: String,
  @SerialName("URL") val url: String
) {
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
   * 교육 기간
   */
  val educatePeriod
    get() = if (educateFrom == educateTo) {
      "$educateFrom(${educateFrom.dayName})"
    } else {
      "$educateFrom(${educateFrom.dayName}) ~ $educateTo(${educateTo.dayName})"
    }

  /**
   * 교육 요일, 시간
   */
  val educateDays
    get() = if (educateFrom == educateTo) {
      educateTime
    } else {
      StringBuilder().apply {
        append("\n")
        if (!monday.isNullOrBlank()) append("월")
        if (!tuesday.isNullOrBlank()) append("화")
        if (!wednesday.isNullOrBlank()) append("수")
        if (!thursday.isNullOrBlank()) append("목")
        if (!friday.isNullOrBlank()) append("금")
        if (!saturday.isNullOrBlank()) append("토")
        if (!sunday.isNullOrBlank()) append("일")
        append("\n")
        append(educateTime)
      }.toString()
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
    get() = "${spareNum.toInt()}/${collectNum.toInt()}명"

  /**
   * 수강료
   */
  val fee
    get() = if (educateFee > 0f) {
      "${educateFee.comma}원"
    } else {
      "무료"
    }

  /**
   * 접수 방법
   */
  val howToRegister
    get() = mutableListOf<String>().apply {
      if (visitReceiveFlag == "Y") add("방문")
      if (onlineReceiveFlag == "Y") add("온라인")
      if (listOf(visitReceiveFlag, onlineReceiveFlag).all { it == "N" }) add("접수방법 확인")
    }.joinToString()
}
