package com.dimanem.security.example.demo

import android.app.Application
import com.dimanem.security.example.demo.di.appModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class App: Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin(this, listOf(appModule))

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}