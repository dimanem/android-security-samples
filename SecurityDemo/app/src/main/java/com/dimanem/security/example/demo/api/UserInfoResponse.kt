package com.dimanem.security.example.demo.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserInfoResponse {
  @SerializedName("firstname")
  @Expose
  var firstname: String = ""

  @SerializedName("lastname")
  @Expose
  var lastname: String = ""

  @SerializedName("job")
  @Expose
  var jobTitle: String = ""

  @SerializedName("email")
  @Expose
  var email: String = ""

  @SerializedName("image")
  @Expose
  var imageUrl: String = ""

  var throwable: Throwable? = null

  fun getFullName(): String = "$firstname $lastname"
}