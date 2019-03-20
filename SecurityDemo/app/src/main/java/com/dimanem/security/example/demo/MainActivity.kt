package com.dimanem.security.example.demo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.dimanem.security.example.demo.api.UserInfoResponse
import com.dimanem.security.example.demo.login.LoginActivity
import com.dimanem.security.example.demo.userInfo.UserInfoViewModel
import com.dimanem.security.example.demo.util.TokenStorage
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_user_details.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

  private val userInfoViewModel: UserInfoViewModel by viewModel()
  private val storage: TokenStorage by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    showToast("Logged In!")

    tvSecret.text = "Token: \n${storage.getToken()}"
    userInfoViewModel.userInfoLiveData.observe({ lifecycle }) {
      handleUserInfoResponse(it)
    }
    userInfoViewModel.fetchUserInfo()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.main_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.logout -> {
        logout()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun handleUserInfoResponse(it: UserInfoResponse?) {
    if (it == null) {
      showToast("Failed to get user data - empty response")
    } else {
      if (it.throwable != null) {
        showToast("Failed to get user data ${it!!.throwable!!.localizedMessage}")
      } else {
        tvUserName.text = it.getFullName()
        tvJobTitle.text = it.jobTitle
        tvEmail.text = it.email
        if (!it.imageUrl.isNullOrEmpty()) {
          ImageLoader.getInstance().displayImage(it.imageUrl, ivUserImage)
        }
      }
    }
  }

  private fun logout() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.action = LoginActivity.INTENT_ACTION_LOGOUT
    startActivity(intent)
    finish()
  }
}
