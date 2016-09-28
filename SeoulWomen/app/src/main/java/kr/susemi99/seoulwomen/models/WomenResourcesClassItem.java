package kr.susemi99.seoulwomen.models;

import com.google.gson.annotations.SerializedName;

public class WomenResourcesClassItem {
  @SerializedName("list_total_count")
  public int listTotalCount;

  @SerializedName("RESULT")
  public ResultItem result;

  @SerializedName("row")
  public RowItem[] rows;

}
