package com.dimanem.security.example.demo.di

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dimanem.security.example.demo.api.APIService
import com.dimanem.security.example.demo.SERVER_URL
import com.dimanem.security.example.demo.util.SecurityUtils
import com.dimanem.security.example.demo.util.Storage
import com.dimanem.security.example.demo.login.LoginViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

  viewModel { LoginViewModel(get(), get()) }
  single { Storage(get()) }
  single { SecurityUtils(androidContext()) }
  single { PreferenceManager.getDefaultSharedPreferences(androidContext()) as SharedPreferences }
  single {
    createWebService<APIService>(
      get(),
      SERVER_URL
    )
  }
  single { createOkHttpClient() }
}

private fun createOkHttpClient(): OkHttpClient {
  val httpLoggingInterceptor = HttpLoggingInterceptor()
  httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
  return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
}

private inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
  val retrofit = Retrofit.Builder()
    .baseUrl(url)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
  return retrofit.create(T::class.java)
}
