package kr.susemi99.seoulwomen.models.bases;

import com.google.gson.annotations.SerializedName;

import kr.susemi99.seoulwomen.models.ResultItem;
import kr.susemi99.seoulwomen.models.RowItem;

public class BaseClassItem
{
  @SerializedName("list_total_count")
  public Integer listTotalCount;

  @SerializedName("RESULT")
  public ResultItem resullt;

  @SerializedName("row")
  public RowItem[] rows;

}
