package kr.susemi99.seoulwomen.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.susemi99.seoulwomen.util.serializer.SkipInvalidRowsSerializer

@Serializable
data class WomenResourcesClassItem(
  @SerialName("list_total_count") val listTotalCount: Int,
  @SerialName("RESULT") val result: ResultItem,
  @Serializable(with = SkipInvalidRowsSerializer::class) @SerialName("row") val rows: List<RowItem>
)
