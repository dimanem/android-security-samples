package com.dimanem.security.example.demo

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dimanem.security.example.demo.api.APIService
import com.dimanem.security.example.demo.util.TokenStorage
import com.dimanem.security.example.demo.login.LoginViewModel
import com.dimanem.security.example.demo.userInfo.UserInfoViewModel
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

  // View models
  viewModel { LoginViewModel(get(), get()) }
  viewModel { UserInfoViewModel(get(), get()) }

  // API
  single { createApiService(get()) }
  single { createOkHttpClient() }

  // Storage
  single { TokenStorage(get()) }
  single { PreferenceManager.getDefaultSharedPreferences(androidContext()) as SharedPreferences }
}

private fun createOkHttpClient(): OkHttpClient {
  val httpLoggingInterceptor = HttpLoggingInterceptor()
  httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

  val certificatePinner = CertificatePinner.Builder()
    .add(BASE_URL, MY_SSL_CERTIFICATE)
    .build()

  return OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .certificatePinner(certificatePinner) // SSL pinning
    .build()
}

private fun createApiService(okHttpClient: OkHttpClient): APIService {
  val retrofit = Retrofit.Builder()
    .baseUrl("https://$BASE_URL")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
  return retrofit.create(APIService::class.java)
}