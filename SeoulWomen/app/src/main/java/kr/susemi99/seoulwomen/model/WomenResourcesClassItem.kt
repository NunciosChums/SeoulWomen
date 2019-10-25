package kr.susemi99.seoulwomen.model

import com.google.gson.annotations.SerializedName

data class WomenResourcesClassItem(
  @SerializedName("list_total_count") val listTotalCount: Int,
  @SerializedName("RESULT") val result: ResultItem,
  @SerializedName("row") val rows: List<RowItem>
)
