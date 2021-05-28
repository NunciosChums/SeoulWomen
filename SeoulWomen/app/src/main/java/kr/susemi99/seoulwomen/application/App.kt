package kr.susemi99.seoulwomen.application

import android.app.Application
import com.chibatching.kotpref.Kotpref
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kr.susemi99.seoulwomen.util.Logg
import rxdogtag2.RxDogTag

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
    RxJavaPlugins.setErrorHandler { Logg.w(it) }
  }
}