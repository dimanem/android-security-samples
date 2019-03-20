package com.dimanem.security.example.demo.userInfo

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dimanem.security.example.demo.api.APIService
import com.dimanem.security.example.demo.api.UserInfoResponse
import com.dimanem.security.example.demo.util.TokenStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserInfoViewModel(private val storage: TokenStorage, private val apiService: APIService) :
  ViewModel() {

  val userInfoLiveData = MutableLiveData<UserInfoResponse>()

  fun fetchUserInfo() {
    val token = storage.getToken()
    if (token == null) {
      userInfoLiveData.postValue(UserInfoResponse().apply { Throwable("Missing token!") })
      return
    }

    val userInfoStream = apiService.getUserInfo(token)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .onErrorReturn { UserInfoResponse().apply { throwable = it } }

    compositeDisposable.add(userInfoStream.subscribe { userInfoLiveData.postValue(it) })
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }

  private val compositeDisposable = CompositeDisposable()
}