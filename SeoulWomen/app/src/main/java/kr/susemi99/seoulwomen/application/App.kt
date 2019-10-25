package kr.susemi99.seoulwomen.application

import android.app.Application
import android.util.Log
import com.chibatching.kotpref.Kotpref
import com.uber.rxdogtag.RxDogTag
import io.reactivex.plugins.RxJavaPlugins

class App : Application() {
  companion object {
    lateinit var instance: App
      private set
  }

  override fun onCreate() {
    super.onCreate()
    instance = this

    Kotpref.init(applicationContext)
    RxDogTag.install()
    RxJavaPlugins.setErrorHandler { Log.w("APP#", it) }
  }
}