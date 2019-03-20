package com.dimanem.security.example.demo.api

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface APIService {

  @FormUrlEncoded
  @POST("/login")
  fun login(@Field("username") userName: String,
    @Field("password") password: String): Observable<LoginResponse>

  @GET("/secret")
  fun getUserInfo(@Header("Token") token: String): Observable<UserInfoResponse>
}