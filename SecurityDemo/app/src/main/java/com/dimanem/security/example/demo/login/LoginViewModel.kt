package com.dimanem.security.example.demo.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dimanem.security.example.demo.api.APIService
import com.dimanem.security.example.demo.api.LoginResponse
import com.dimanem.security.example.demo.util.TokenStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

interface ILoginViewModel {
  val loginSuccessLiveData: LiveData<Unit>
  val loginFailedLiveData: LiveData<Throwable>
  fun login(userName: String, password: String)
  fun logout()
  fun isLoggedIn(): Boolean
}

class LoginViewModel(private val storage: TokenStorage, private val apiService: APIService) :
  ILoginViewModel, ViewModel() {

  override val loginSuccessLiveData = MutableLiveData<Unit>()
  override val loginFailedLiveData = MutableLiveData<Throwable>()

  override fun login(userName: String, password: String) {
    val loginStream = apiService.login(userName, password)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .onErrorReturn { LoginResponse().apply { throwable = it } }
      .replay().refCount()

    val loginSuccess = loginStream
      .filter { it.throwable == null && it.token.isNotEmpty() }
      .doOnNext { storage.saveToken(it.token) }
      .subscribe { loginSuccessLiveData.postValue(Unit) }

    val loginError = loginStream
      .filter { it.throwable != null || it.token.isEmpty() }
      .map { it.throwable ?: Exception("Missing token") }
      .subscribe { loginFailedLiveData.postValue(it) }

    compositeDisposable.addAll(loginSuccess, loginError)
  }

  override fun isLoggedIn() = storage.getToken() != null

  override fun logout() = storage.saveToken(null)

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }

  private val compositeDisposable = CompositeDisposable()
}