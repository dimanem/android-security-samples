package com.dimanem.security.example.demo.api

import com.dimanem.security.example.demo.login.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIService {

  @FormUrlEncoded
  @POST("/login")
  fun login(@Field("username") userName: String,
    @Field("password") password: String): Observable<LoginResponse>
}