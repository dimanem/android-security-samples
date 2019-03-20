package com.dimanem.security.example.demo.util

import android.content.SharedPreferences

class TokenStorage(private val sharedPreferences: SharedPreferences) {

  fun getToken(): String? {
    return sharedPreferences.getString("token", null)
  }

  fun saveToken(token: String?) {
    sharedPreferences.edit().putString("token", token).apply()
  }
}