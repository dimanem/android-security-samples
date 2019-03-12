package com.dimanem.security.example.demo.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
  @SerializedName("token")

  @Expose
  var token: String = ""

  var throwable: Throwable? = null
}