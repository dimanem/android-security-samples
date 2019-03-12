package com.dimanem.security.example.demo.util

import android.content.Context
import android.util.Log
import java.io.File

class SecurityUtils(val context: Context) {

  fun isRootedDevice(): Boolean {
    val rootFiles = arrayOf(
      "/system/app/Superuser.apk",
      "/sbin/su",
      "/system/bin/su",
      "/system/xbin/su",
      "/data/local/xbin/su",
      "/data/local/bin/su",
      "/system/sd/xbin/su",
      "/system/bin/failsafe/su",
      "/data/local/su",
      "/su/bin/su"
    )
    for (path in rootFiles) {
      if (File(path).exists()) {
        Log.e("Root", "File exists: $path")
        return true
      }
    }
    return false
  }
}