package kr.susemi99.seoulwomen.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

public class MyApp extends MultiDexApplication {
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
  }

  public static Context context() {
    return context;
  }
}
