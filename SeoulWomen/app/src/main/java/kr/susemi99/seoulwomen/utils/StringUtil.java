package kr.susemi99.seoulwomen.utils;

import java.text.NumberFormat;

/**
 * Created by susemi99 on 2016. 3. 5..
 */
public class StringUtil {
  public static String addComma(float value) {
    return addComma((int) value);
  }

  public static String addComma(int value) {
    return NumberFormat.getNumberInstance().format(value);
  }
}
