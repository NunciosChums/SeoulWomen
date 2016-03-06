package kr.susemi99.seoulwomen.managers;

import android.content.Context;
import android.content.SharedPreferences;

import kr.susemi99.seoulwomen.application.MyApp;

/**
 * Created by susemi99 on 2016. 3. 5..
 */
public class BasePreferenceHelper
{
  private SharedPreferences _sharedPreferences;


  protected BasePreferenceHelper()
  {
    super();
    _sharedPreferences = MyApp.context().getSharedPreferences(MyApp.context().getPackageName(), Context.MODE_PRIVATE);
  }


  private SharedPreferences.Editor editor()
  {
    return _sharedPreferences.edit();
  }


  /**
   * key 수동 설정
   *
   * @param key   키 값
   * @param value 내용
   */
  protected void put(String key, String value)
  {
    editor().putString(key, value).commit();
  }


  /**
   * String 값 가져오기
   *
   * @param key 키 값
   * @return String (기본값 null)
   */
  protected String get(String key)
  {
    return _sharedPreferences.getString(key, null);
  }

  /**
   * key 설정
   *
   * @param key   키 값
   * @param value 내용
   */
  protected void put(String key, int value)
  {
    editor().putInt(key, value).commit();
  }


  /**
   * int 값 가져오기
   *
   * @param key      키 값
   * @param defValue 기본값
   * @return int
   */
  protected int get(String key, int defValue)
  {
    return _sharedPreferences.getInt(key, defValue);
  }
}
