package kr.susemi99.seoulwomen.model

import com.google.gson.annotations.SerializedName

data class WomenResourcesClassParentItem(
  @SerializedName(
    value = "SeoulDisableWomenResourcesClass",
    alternate = [
      "SeoulDongjakWomenResourcesClass",
      "SeoulSeochoWomenResourcesClass",
      "SeoulSeongdongWomenResourcesClass",
      "SeoulSongpaWomenResourcesClass",
      "SeoulYongSanWomenResourcesClass",
      "SeoulJungNangWomenResourcesClass",
      "SeoulNambuWomenResourcesClass",
      "SeoulBukbuWomenResourcesClass",
      "SeoulSeobuWomenResourcesClass",
      "SeoulJungBuWomenResourcesClass"
    ]
  )
  val classItem: WomenResourcesClassItem,

  @SerializedName("RESULT") val result: ResultItem?
)
