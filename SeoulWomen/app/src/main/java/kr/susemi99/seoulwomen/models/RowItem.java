package kr.susemi99.seoulwomen.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import kr.susemi99.seoulwomen.utils.DateFormatter;
import kr.susemi99.seoulwomen.utils.StringUtil;

public class RowItem {
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
  public String visitReceiveFlag;
  @SerializedName("ONLINE_RECEIVE_FLAG")
  public String onlineReceiveFlag;
  @SerializedName("URL")
  public String url;

  public String displayReceiveFrom() {
    return DateFormatter.parseDate(receiveFrom, "yyyyMMdd");
  }

  public String displayReceiveTo() {
    return DateFormatter.parseDate(receiveTo, "yyyyMMdd");
  }

  public String displayEducateFrom() {
    return DateFormatter.parseDate(educateFrom, "yyyyMMdd");
  }

  public String displayEducateTo() {
    return DateFormatter.parseDate(educateTo, "yyyyMMdd");
  }

  public String displaySpareNum() {
    return StringUtil.addComma(Float.parseFloat(spareNum));
  }

  public String displayDays() {
    StringBuilder builder = new StringBuilder();
    if(!TextUtils.isEmpty(monday)) {
      builder.append("월");
    }
    if(!TextUtils.isEmpty(tuesday)) {
      builder.append("화");
    }
    if(!TextUtils.isEmpty(wednesday)) {
      builder.append("수");
    }
    if(!TextUtils.isEmpty(thursday)) {
      builder.append("목");
    }
    if(!TextUtils.isEmpty(friday)) {
      builder.append("금");
    }
    if(!TextUtils.isEmpty(saturday)) {
      builder.append("토");
    }
    if(!TextUtils.isEmpty(sunday)) {
      builder.append("일");
    }
    return builder.toString();
  }

  public String displayCollectNum() {
    return StringUtil.addComma(Float.parseFloat(collectNum));
  }

  public String displayEducateFee() {
    float floatFee = Float.parseFloat(educateFee);
    int intFee = (int) floatFee;
    if(intFee == 0) {
      return "무료";
    } else {
      return StringUtil.addComma(intFee) + "원";
    }
  }

  public String displayHowToRegist() {
    ArrayList<String> result = new ArrayList<>();
    if(visitReceiveFlag.equals("Y")) {
      result.add("방문");
    }
    if(onlineReceiveFlag.equals("Y")) {
      result.add("온라인");
    }
    return TextUtils.join(", ", result);
  }
}
