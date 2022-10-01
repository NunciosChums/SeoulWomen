package kr.susemi99.seoulwomen

import android.app.Application
import com.chibatching.kotpref.Kotpref
import dagger.hilt.android.HiltAndroidApp
import kr.susemi99.seoulwomen.util.DebugLogTree
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(DebugLogTree())
    }

    Kotpref.init(this)
  }
}