package kr.susemi99.seoulwomen.api

import kr.susemi99.seoulwomen.model.WomenResourcesClassParentItem
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
  @GET("{area_value}/{start_index}/{end_index}")
  suspend fun list(@Path("area_value") areaValue: String, @Path("start_index") startIndex: Int, @Path("end_index") endIndex: Int): WomenResourcesClassParentItem
}