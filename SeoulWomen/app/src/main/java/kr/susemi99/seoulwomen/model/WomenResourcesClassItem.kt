package kr.susemi99.seoulwomen.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WomenResourcesClassItem(
  @SerialName("list_total_count") val listTotalCount: Int,
  @SerialName("RESULT") val result: ResultItem,
  @SerialName("row") val rows: List<RowItem>
)
