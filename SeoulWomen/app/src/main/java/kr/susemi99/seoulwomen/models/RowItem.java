package kr.susemi99.seoulwomen.models;

import com.google.gson.annotations.SerializedName;

public class RowItem
{
  @SerializedName("CLASS_CODE")
  public String classCode;
  @SerializedName("CLASS_NAME")
  public String className;
  @SerializedName("ORGAN_CODE")
  public String organCode;
  @SerializedName("ORGAN_NAME")
  public String organName;
  @SerializedName("DIFFICULTY")
  public String difficulty;
  @SerializedName("DIFFICULTY_NAME")
  public String difficultyName;
  @SerializedName("RECEIVE_FROM")
  public String receiveFrom;
  @SerializedName("RECEIVE_TO")
  public String receiveTo;
  @SerializedName("RECEIVE_TIME_FROM")
  public String receiveTimeFrom;
  @SerializedName("RECEIVE_TIME_TO")
  public String receiveTimeTo;
  @SerializedName("EDUCATE_FROM")
  public String educateFrom;
  @SerializedName("EDUCATE_TO")
  public String educateTo;
  @SerializedName("EDUCATE_TIME_FROM")
  public String educateTimeFrom;
  @SerializedName("EDUCATE_TIME_TO")
  public String educateTimeTo;
  @SerializedName("MONDAY")
  public String monday;
  @SerializedName("TUESDAY")
  public String tuesday;
  @SerializedName("WEDNESDAY")
  public String wednesday;
  @SerializedName("THURSDAY")
  public String thursday;
  @SerializedName("FRIDAY")
  public String friday;
  @SerializedName("SATURDAY")
  public String saturday;
  @SerializedName("SUNDAY")
  public String sunday;
  @SerializedName("COLLECT_NUM")
  public String collectNum;
  @SerializedName("SPARE_NUM")
  public String spareNum;
  @SerializedName("EDUCATE_FEE")
  public String educateFee;
  @SerializedName("VISIT_RECEIVE_FLAG")
  public String visitFeceiveFlag;
  @SerializedName("ONLINE_RECEIVE_FLAG")
  public String onlineReceiveFlag;
  @SerializedName("URL")
  public String url;
}
