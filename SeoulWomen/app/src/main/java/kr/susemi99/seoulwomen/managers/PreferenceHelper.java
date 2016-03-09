package kr.susemi99.seoulwomen.managers;

/**
 * Created by susemi99 on 2016. 3. 5..
 */
public class PreferenceHelper extends BasePreferenceHelper
{
  private static final String LAST_SELECTED_AREA_VALUE = "last_selected_area_value_v1";
  private static final String LAST_SELECTED_AREA_NAME = "last_selected_area_name_v1";

  public static synchronized PreferenceHelper instance()
  {
    return new PreferenceHelper();
  }

  public void lastSelectedAreaValue(String value)
  {
    put(LAST_SELECTED_AREA_VALUE, value);
  }

  public String lastSelectedAreaValue()
  {
    return get(LAST_SELECTED_AREA_VALUE);
  }

  public void lastSelectedAreaName(String name)
  {
    put(LAST_SELECTED_AREA_NAME, name);
  }

  public String lastSelectedAreaName()
  {
    return get(LAST_SELECTED_AREA_NAME);
  }
}
