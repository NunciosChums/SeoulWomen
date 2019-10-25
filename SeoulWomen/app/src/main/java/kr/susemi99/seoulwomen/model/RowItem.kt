package kr.susemi99.seoulwomen.model

import com.google.gson.annotations.SerializedName
import kr.susemi99.seoulwomen.extension.toCommaString
import kr.susemi99.seoulwomen.util.DateFormatter
import kr.susemi99.seoulwomen.util.DateFormatter.YEAR_MONTH_DAY1
import kr.susemi99.seoulwomen.util.DateFormatter.YEAR_MONTH_DAY2

data class RowItem(
  @SerializedName("CLASS_CODE") val classCode: String,
  @SerializedName("CLASS_NAME") val className: String,
  @SerializedName("ORGAN_CODE") val organCode: String,
  @SerializedName("ORGAN_NAME") val organName: String,
  @SerializedName("DIFFICULTY") val difficulty: String,
  @SerializedName("DIFFICULTY_NAME") val difficultyName: String,
  @SerializedName("RECEIVE_FROM") val receiveFrom: String,
  @SerializedName("RECEIVE_TO") val receiveTo: String,
  @SerializedName("RECEIVE_TIME_FROM") val receiveTimeFrom: String,
  @SerializedName("RECEIVE_TIME_TO") val receiveTimeTo: String,
  @SerializedName("EDUCATE_FROM") val educateFrom: String,
  @SerializedName("EDUCATE_TO") val educateTo: String,
  @SerializedName("EDUCATE_TIME_FROM") val educateTimeFrom: String,
  @SerializedName("EDUCATE_TIME_TO") val educateTimeTo: String,
  @SerializedName("MONDAY") val monday: String?,
  @SerializedName("TUESDAY") val tuesday: String?,
  @SerializedName("WEDNESDAY") val wednesday: String?,
  @SerializedName("THURSDAY") val thursday: String?,
  @SerializedName("FRIDAY") val friday: String?,
  @SerializedName("SATURDAY") val saturday: String?,
  @SerializedName("SUNDAY") val sunday: String?,
  @SerializedName("COLLECT_NUM") val collectNum: String,
  @SerializedName("SPARE_NUM") val spareNum: String,
  @SerializedName("EDUCATE_FEE") val educateFee: String,
  @SerializedName("VISIT_RECEIVE_FLAG") val visitReceiveFlag: String,
  @SerializedName("ONLINE_RECEIVE_FLAG") val onlineReceiveFlag: String,
  @SerializedName("URL") val url: String
) {

  fun displayReceiveFrom() = DateFormatter.format(receiveFrom, YEAR_MONTH_DAY1, YEAR_MONTH_DAY2)
  fun displayReceiveTo() = DateFormatter.format(receiveTo, YEAR_MONTH_DAY1, YEAR_MONTH_DAY2)
  fun displayEducateFrom() = DateFormatter.format(educateFrom, YEAR_MONTH_DAY1, YEAR_MONTH_DAY2)
  fun displayEducateTo() = DateFormatter.format(educateTo, YEAR_MONTH_DAY1, YEAR_MONTH_DAY2)
  fun displaySpareNum() = spareNum.toCommaString()
  fun displayCollectNum() = collectNum.toCommaString()

  fun displayDays(): String {
    val builder = StringBuilder()
    if (!monday.isNullOrBlank()) builder.append("월")
    if (!tuesday.isNullOrBlank()) builder.append("화")
    if (!wednesday.isNullOrBlank()) builder.append("수")
    if (!thursday.isNullOrBlank()) builder.append("목")
    if (!friday.isNullOrBlank()) builder.append("금")
    if (!saturday.isNullOrBlank()) builder.append("토")
    if (!sunday.isNullOrBlank()) builder.append("일")
    return builder.toString()
  }

  fun displayEducateFee() = if (educateFee.toFloat() > 0f) {
    "${educateFee.toCommaString()}원"
  } else {
    "무료"
  }

  fun displayHowToRegist(): String {
    val result = arrayListOf<String>()
    if (visitReceiveFlag == "Y") {
      result.add("방문")
    }
    if (onlineReceiveFlag == "Y") {
      result.add("온라인")
    }
    return result.joinToString(", ")
  }
}
