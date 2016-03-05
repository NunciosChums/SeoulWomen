package kr.susemi99.seoulwomen.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by susemi99 on 2016. 3. 5..
 */
public class MyApp extends Application
{
  private static Context context;

  @Override
  public void onCreate()
  {
    super.onCreate();
    context = getApplicationContext();
  }

  public static Context context()
  {
    return context;
  }
}
