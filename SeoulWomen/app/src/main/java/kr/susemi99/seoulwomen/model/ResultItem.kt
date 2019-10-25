package kr.susemi99.seoulwomen.model

import com.google.gson.annotations.SerializedName

data class ResultItem(
  @SerializedName("CODE") val code: String,
  @SerializedName("MESSAGE") val message: String
)
