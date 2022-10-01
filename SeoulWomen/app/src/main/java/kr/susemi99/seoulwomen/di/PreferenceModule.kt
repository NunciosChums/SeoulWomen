package kr.susemi99.seoulwomen.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.susemi99.seoulwomen.util.preference.AppPreference
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PreferenceModule {
  @Provides
  @Singleton
  fun provideAppPreference(application: Application) = AppPreference(application)
}