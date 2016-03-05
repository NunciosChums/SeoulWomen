package kr.susemi99.seoulwomen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by susemi99 on 2016. 3. 5..
 */
public class DateFormatter
{
  public static String parseDate(String dateStr, String pattern)
  {
    String result = dateStr;

    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat(pattern);
      Date date = formatter.parse(dateStr);
      result = format(date.getTime(), "yyyy.MM.dd");
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    return result;
  }

  private static String format(long timeInMills, String pattern)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    return formatter.format(timeInMills);
  }
}
