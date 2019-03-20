package com.dimanem.security.root.example.app2

import android.app.Application
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

class MaliciousApp : Application() {
  override fun onCreate() {
    super.onCreate()
    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this))
  }
}