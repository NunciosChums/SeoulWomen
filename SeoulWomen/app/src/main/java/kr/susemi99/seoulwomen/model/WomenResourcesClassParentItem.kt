package kr.susemi99.seoulwomen.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WomenResourcesClassParentItem(
  @SerialName("SeoulDisableWomenResourcesClass") val item1: WomenResourcesClassItem? = null,
  @SerialName("SeoulDongjakWomenResourcesClass") val item2: WomenResourcesClassItem? = null,
  @SerialName("SeoulSeochoWomenResourcesClass") val item3: WomenResourcesClassItem? = null,
  @SerialName("SeoulSeongdongWomenResourcesClass") val item4: WomenResourcesClassItem? = null,
  @SerialName("SeoulSongpaWomenResourcesClass") val item5: WomenResourcesClassItem? = null,
  @SerialName("SeoulYongSanWomenResourcesClass") val item6: WomenResourcesClassItem? = null,
  @SerialName("SeoulJungNangWomenResourcesClass") val item7: WomenResourcesClassItem? = null,
  @SerialName("SeoulNambuWomenResourcesClass") val item8: WomenResourcesClassItem? = null,
  @SerialName("SeoulBukbuWomenResourcesClass") val item9: WomenResourcesClassItem? = null,
  @SerialName("SeoulSeobuWomenResourcesClass") val item10: WomenResourcesClassItem? = null,
  @SerialName("SeoulJungBuWomenResourcesClass") val item11: WomenResourcesClassItem? = null,
  @SerialName("RESULT") val result: ResultItem? = null
) {
  val classItem: WomenResourcesClassItem?
    get() = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11).firstOrNull { it != null }
}
