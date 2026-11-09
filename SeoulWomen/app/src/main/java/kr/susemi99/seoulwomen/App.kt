package kr.susemi99.seoulwomen

import android.app.Application
import com.chibatching.kotpref.Kotpref
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    Kotpref.init(this)
  }
}